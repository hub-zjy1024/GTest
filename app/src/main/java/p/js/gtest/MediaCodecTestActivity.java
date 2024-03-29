package p.js.gtest;

import android.content.pm.ActivityInfo;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import butterknife.BindView;
import butterknife.ButterKnife;
import p.js.gtest.media.AvcUtils;
import p.js.gtest.media.Decoder;
import p.js.gtest.media.Encoder;

public class MediaCodecTestActivity extends AppCompatActivity implements SurfaceHolder.Callback, View.OnClickListener, Camera.PreviewCallback {

    @BindView(R.id.surfaceView_encode)
    SurfaceView surfaceViewEncode;
    @BindView(R.id.surfaceView_decode)
    SurfaceView surfaceViewDecode;
    @BindView(R.id.btnStart)
    Button btnStart;
    @BindView(R.id.btnStop)
    Button btnStop;
    @BindView(R.id.capture)
    TextView capture;
    private int width;
    private int height;
    private int bitrate;
    private int framerate;
    private int captureFrame;
    private Camera mCamera;
    private Queue<PreviewBufferInfo> mPreviewBuffers_clean;
    private Queue<PreviewBufferInfo> mPreviewBuffers_dirty;
    private Queue<PreviewBufferInfo> mDecodeBuffers_clean;
    private Queue<PreviewBufferInfo> mDecodeBuffers_dirty;
    private int PREVIEW_POOL_CAPACITY = 5;
    private int format;
    private int DECODE_UNI_SIZE = 1024 * 1024;
    private byte[] mAvcBuf = new byte[1024 * 1024];
    private final int MSG_ENCODE = 0;
    private final int MSG_DECODE = 1;
    private String TAG = "zjy_MediaCodecTestActivity";
    private long mLastTestTick = 0;
    private Object mAvcEncLock;
    private Object mDecEncLock;
    private Decoder mDecoder;
    private Encoder mEncoder;
    private CodecThread codecThread;
    private Handler codecHandler;
    private byte[] mRawData;

    private int preRawSize = 0;
    private DatagramSocket socket;
    private DatagramPacket packet;
    private byte[] sps_pps;
    private byte[] mPacketBuf = new byte[1024 * 1024];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_codec_test);
        ButterKnife.bind(this);
        //初始化参数
        initParams();
        //设置监听事件
        btnStart.setOnClickListener(this);
        btnStop.setOnClickListener(this);
        surfaceViewEncode.getHolder().addCallback(this);
    }

//    @OnClick({R.id.btnStart, R.id.btnStop})
//    public void mclick(){
//
//    }
    /**
     * 初始化参数，包括帧率、颜色、比特率，视频宽高等
     */
    private void initParams() {
        width = 384;
        height = 288;
        bitrate = 1500000;
        framerate = 30;
        captureFrame = 0;
        format = ImageFormat.YV12;
        mAvcEncLock = new Object();
        mDecEncLock = new Object();
    }

    @Override
    protected void onResume() {
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        super.onResume();
    }

    boolean hasSurface = false;
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        hasSurface = true;
        Log.e("zjy", getClass() + "->surfaceCreated(): start==");
        mCamera = Camera.open(0);
        initQueues();
        initEncoder();
        initCodecThread();

        startPreview();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnStart:

                if (hasSurface) {
                    mCamera = Camera.open(0);
                    Log.e("zjy", getClass() + "->onClick(): ==hasSurface");
                    initQueues();
                    startPreview();
                    initEncoder();
                    initCodecThread();
                } else {
                    Log.e("zjy", getClass() + "->onClick(): ==wait surface");


                }
                break;
            case R.id.btnStop:
                releaseCodecThread();
                releseEncoderAndDecoder();
                releaseCamera();
                releaseQueue();
                break;
        }
    }

    /**
     * 释放队列资源
     */
    private void releaseQueue() {
        if (mPreviewBuffers_clean != null){
            mPreviewBuffers_clean.clear();
            mPreviewBuffers_clean = null;
        }
        if (mPreviewBuffers_dirty != null){
            mPreviewBuffers_dirty.clear();
            mPreviewBuffers_dirty = null;
        }
        if (mDecodeBuffers_clean != null){
            mDecodeBuffers_clean.clear();
            mDecodeBuffers_clean = null;
        }
        if (mDecodeBuffers_dirty != null){
            mDecodeBuffers_dirty.clear();
            mDecodeBuffers_dirty = null;
        }
    }

    /**
     * 释放摄像头资源
     */
    private void releaseCamera() {
        if(mCamera != null){
            mCamera.setPreviewCallbackWithBuffer(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    private void releseEncoderAndDecoder() {
        if(mEncoder != null){
            mEncoder.flush();
            mEncoder.release();
            mEncoder = null;
        }
        if(mDecoder != null){
            mDecoder.release();
            mDecoder = null;
        }
    }

    private void releaseCodecThread() {
        if (codecHandler != null) {
            codecHandler.getLooper().quit();
        }

        codecHandler = null;
        codecThread = null;
    }

    private void initCodecThread() {
        codecThread = new CodecThread();
        codecThread.start();
    }

    /**
     * 开启预览
     */
    private void startPreview() {
        Log.e("zjy", getClass() + "->startPreview(): ==startPreview");
        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setPreviewFormat(format);
        parameters.setPreviewFrameRate(framerate);
        parameters.setPreviewSize(width,height);
        mCamera.setParameters(parameters);
        try {
            mCamera.setPreviewDisplay(surfaceViewEncode.getHolder());
        } catch (IOException e) {
            e.printStackTrace();
        }
        mCamera.setPreviewCallbackWithBuffer(this);
        mCamera.startPreview();
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        /** 预览的data为null */
        if(data == null) {
            Log.e(TAG,"预览的data为null");
            return;
        }
        Log.e("zjy", getClass() + "->onPreviewFrame():previewLen==" + data.length);
        long curTick = System.currentTimeMillis();
        if (mLastTestTick == 0) {
            mLastTestTick = curTick;
        }
        if (curTick > mLastTestTick + 1000) {
            setCaptureFPSTextView(captureFrame);
            captureFrame = 0;
            mLastTestTick = curTick;
        } else
            captureFrame++;
        synchronized(mAvcEncLock) {
            PreviewBufferInfo info = mPreviewBuffers_clean.poll();    //remove the head of queue
            info.buffer = data;
            info.size = getPreviewBufferSize2(width, height, format);
            info.timestamp = System.currentTimeMillis();
            mPreviewBuffers_dirty.add(info);
            if(mDecoder == null){
                codecHandler.sendEmptyMessage(MSG_ENCODE);
            }
        }
    }

    private void setCaptureFPSTextView(int captureFrame) {
        capture.setText("当前帧率：" + captureFrame);
    }

    private void initEncoder() {
        mEncoder = new Encoder();
        try {
            mEncoder.init();
            mEncoder.configure(width,height,bitrate,framerate);
//            mEncoder.configure2(width, height, surfaceViewEncode.getHolder().getSurface(), bitrate, framerate);
            mEncoder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 初始化各种队列
     */
    private void initQueues() {
        if (mPreviewBuffers_clean == null)
            mPreviewBuffers_clean = new LinkedList<>();
        if (mPreviewBuffers_dirty == null)
            mPreviewBuffers_dirty = new LinkedList<>();
        int oneFrameSize = getPreviewBufferSize2(width, height, format);
        for (int i = 0; i < PREVIEW_POOL_CAPACITY; i++) {
            byte[] mem = new byte[oneFrameSize];
            mCamera.addCallbackBuffer(mem);    //ByteBuffer.array is a reference, not a copy
            PreviewBufferInfo info = new PreviewBufferInfo();
            info.buffer = null;
            info.size = 0;
            info.timestamp = 0;
            mPreviewBuffers_clean.add(info);
        }
        if (mDecodeBuffers_clean == null)
            mDecodeBuffers_clean = new LinkedList<>();
        if (mDecodeBuffers_dirty == null)
            mDecodeBuffers_dirty = new LinkedList<>();
        for (int i = 0; i < PREVIEW_POOL_CAPACITY; i++) {
            PreviewBufferInfo info = new PreviewBufferInfo();
            info.buffer = new byte[DECODE_UNI_SIZE];
            info.size = 0;
            info.timestamp = 0;
            mDecodeBuffers_clean.add(info);
        }
    }

    private int getPreviewBufferSize2(int width, int height, int format) {
        if (preRawSize == 0) {
            int previewBufferSize = getPreviewBufferSize(width, height, format);
            preRawSize = previewBufferSize;
        }
        return preRawSize;
    }
    /**
     * 获取预览buffer的大小
     * @param width 预览宽
     * @param height 预览高
     * @param format 预览颜色格式
     * @return 预览buffer的大小
     */

    private int getPreviewBufferSize(int width, int height, int format) {
        int size = 0;
        switch (format) {
            case ImageFormat.YV12: {
                int yStride = (int) Math.ceil(width / 16.0) * 16;
                int uvStride = (int) Math.ceil((yStride / 2) / 16.0) * 16;
                int ySize = yStride * height;
                int uvSize = uvStride * height / 2;
                size = ySize + uvSize * 2;
            }
            break;

            case ImageFormat.NV21: {
                float bytesPerPix = (float) ImageFormat.getBitsPerPixel(format) / 8;
                size = (int) (width * height * bytesPerPix);
            }
            break;
        }

        return size;
    }

    private void swapYV12toI420(byte[] yv12bytes, byte[] i420bytes, int width, int height) {
        System.arraycopy(yv12bytes, 0, i420bytes, 0, width * height);
        System.arraycopy(yv12bytes, width * height + width * height / 4, i420bytes, width * height, width * height / 4);
        System.arraycopy(yv12bytes, width * height, i420bytes, width * height + width * height / 4, width * height / 4);
    }

    private class PreviewBufferInfo {
        public byte[] buffer;
        public int size;
        public long timestamp;
    }

    private class CodecThread extends Thread {
        @Override
        public void run() {
            Looper.prepare();
            codecHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    Log.e("zjy", getClass() + "->handleMessage(): ==msgId=" + msg.what);
                    switch (msg.what) {
                        case MSG_ENCODE:
                            int res = Encoder.BUFFER_OK;
                            synchronized (mAvcEncLock) {
                                if (mPreviewBuffers_dirty != null && mPreviewBuffers_clean != null) {
                                    Iterator<PreviewBufferInfo> ite = mPreviewBuffers_dirty.iterator();
                                    while (ite.hasNext()) {
                                        PreviewBufferInfo info = ite.next();
                                        byte[] data = info.buffer;
                                        int data_size = info.size;
                                        if (format == ImageFormat.YV12) {
                                            if (mRawData == null || mRawData.length < data_size) {
                                                mRawData = new byte[data_size];
                                            }
                                            swapYV12toI420(data, mRawData, width, height);
                                            Log.e("zjy", getClass() + "->handleMessage(): ==swapYV12toI420");
                                        } else {
                                            Log.e(TAG, "preview size MUST be YV12, cur is " + format);
                                            mRawData = data;
                                        }
                                        res = mEncoder.input(mRawData, data_size, info.timestamp);
                                        if (res != Encoder.BUFFER_OK) {
                                            Log.e(TAG, "mEncoder.input, maybe wrong:" + res);
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    AlertDialog.Builder builder =
                                                            new AlertDialog.Builder(MediaCodecTestActivity.this);
                                                    builder.setTitle("异常");
                                                    builder.setMessage("解码异常");
                                                    builder.setCancelable(true);
                                                    builder.show();
                                                }
                                            });
                                            break;        //the rest buffers shouldn't go into encoder, if the previous one get problem
                                        } else {
                                            ite.remove();
                                            mPreviewBuffers_clean.add(info);
                                            if (mCamera != null) {
                                                mCamera.addCallbackBuffer(data);
                                            }
                                        }
                                    }
                                }
                            }


                            while (res == Encoder.BUFFER_OK) {
                                int[] len = new int[1];
                                long[] ts = new long[1];
                                synchronized (mAvcEncLock) {
                                    res = mEncoder.output(mAvcBuf, len, ts);
                                }

                                if (res == Encoder.BUFFER_OK) {
                                    //发送h264
                                    if(sps_pps != null){
                                        send(len[0]);
                                    }

                                    if (mDecodeBuffers_clean != null && mDecodeBuffers_dirty != null) {
                                        synchronized (mAvcEncLock) {
                                            Iterator<PreviewBufferInfo> ite = mDecodeBuffers_clean.iterator();
                                            if (ite.hasNext()) {
                                                PreviewBufferInfo bufferInfo = ite.next();
                                                if (bufferInfo.buffer.length >= len[0]) {
                                                    bufferInfo.timestamp = ts[0];
                                                    bufferInfo.size = len[0];
                                                    System.arraycopy(mAvcBuf, 0, bufferInfo.buffer, 0, len[0]);
                                                    ite.remove();
                                                    mDecodeBuffers_dirty.add(bufferInfo);
                                                } else {
                                                    Log.e(TAG, "decoder uni buffer too small, need " + len[0] + " but has " + bufferInfo.buffer.length);
                                                }
                                            }
                                        }
                                        initDecoder(len);
                                    }
                                }

                            }
                            codecHandler.sendEmptyMessageDelayed(MSG_ENCODE, 30);
                            break;

                        case MSG_DECODE:
                            synchronized (mDecEncLock) {
                                int result = Decoder.BUFFER_OK;

                                //STEP 1: handle input buffer
                                if (mDecodeBuffers_dirty != null && mDecodeBuffers_clean != null) {
                                    Iterator<PreviewBufferInfo> ite = mDecodeBuffers_dirty.iterator();
                                    while (ite.hasNext()) {
                                        PreviewBufferInfo info = ite.next();
                                        result = mDecoder.input(info.buffer, info.size, info.timestamp);
                                        if (result != Decoder.BUFFER_OK) {
                                            break;        //the rest buffers shouldn't go into encoder, if the previous one get problem
                                        } else {
                                            ite.remove();
                                            mDecodeBuffers_clean.add(info);
                                        }
                                    }
                                }

                                int[] len = new int[1];
                                long[] ts = new long[1];
                                while (result == Decoder.BUFFER_OK) {
                                    result = mDecoder.output(null, len, ts);
                                }
                            }
                            codecHandler.sendEmptyMessageDelayed(MSG_DECODE, 30);
                            break;
                    }
                }
            };
            Looper.loop();
        }
    }

    private void send(int len) {
        try {
            if(socket == null) socket = new DatagramSocket();
//            if(packet == null){
//                packet = new DatagramPacket(mPacketBuf,0,sps_pps.length + len);
//                packet.setAddress(InetAddress.getByName("192.168.43.1"));
//                packet.setPort(5006);
//            }

            packet = new DatagramPacket(mPacketBuf,0,sps_pps.length + len);
            packet.setAddress(InetAddress.getByName("192.168.43.1"));
            packet.setPort(5006);
            if(mAvcBuf[4] == 0x65){
                System.arraycopy(sps_pps,0,mPacketBuf,0,sps_pps.length);
                System.arraycopy(mAvcBuf,0,mPacketBuf,sps_pps.length,len);
                len += sps_pps.length;
            }else{
                System.arraycopy(mAvcBuf,0,mPacketBuf,0,len);
            }
            packet.setLength(len);
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initDecoder(int[] len) {
        if(sps_pps == null){
            sps_pps = new byte[len[0]];
            System.arraycopy(mAvcBuf,0,sps_pps,0,len[0]);
        }
        if(mDecoder == null){
            mDecoder = new Decoder();
            try {
                mDecoder.init();
            } catch (IOException e) {
                e.printStackTrace();
            }
            byte[] sps_nal = null;
            int sps_len = 0;
            byte[] pps_nal = null;
            int pps_len = 0;
            ByteBuffer byteb = ByteBuffer.wrap(mAvcBuf, 0, len[0]);
            //SPS
            if (true == AvcUtils.goToPrefix(byteb)) {
                int sps_position = 0;
                int pps_position = 0;
                int nal_type = AvcUtils.getNalType(byteb);
                if (AvcUtils.NAL_TYPE_SPS == nal_type) {
                    Log.d(TAG, "OutputAvcBuffer, AVC NAL type: SPS");
                    sps_position = byteb.position() - AvcUtils.START_PREFIX_LENGTH - AvcUtils.NAL_UNIT_HEADER_LENGTH;
                    //PPS
                    if (true == AvcUtils.goToPrefix(byteb)) {
                        nal_type = AvcUtils.getNalType(byteb);
                        if (AvcUtils.NAL_TYPE_PPS == nal_type) {
                            pps_position = byteb.position() - AvcUtils.START_PREFIX_LENGTH - AvcUtils.NAL_UNIT_HEADER_LENGTH;
                            sps_len = pps_position - sps_position;
                            sps_nal = new byte[sps_len];
                            int cur_pos = byteb.position();
                            byteb.position(sps_position);
                            byteb.get(sps_nal, 0, sps_len);
                            byteb.position(cur_pos);
                            //slice
                            if (true == AvcUtils.goToPrefix(byteb)) {
                                nal_type = AvcUtils.getNalType(byteb);
                                int pps_end_position = byteb.position() - AvcUtils.START_PREFIX_LENGTH - AvcUtils.NAL_UNIT_HEADER_LENGTH;
                                pps_len = pps_end_position - pps_position;
                            } else {
                                pps_len = byteb.position() - pps_position;
                                //pps_len = byteb.limit() - pps_position + 1;
                            }
                            if (pps_len > 0) {
                                pps_nal = new byte[pps_len];
                                cur_pos = byteb.position();
                                byteb.position(pps_position);
                                byteb.get(pps_nal, 0, pps_len);
                                byteb.position(cur_pos);
                            }
                        } else {
                            //Log.d(log_tag, "OutputAvcBuffer, AVC NAL type: "+nal_type);
                            throw new UnsupportedOperationException("SPS is not followed by PPS, nal type :" + nal_type);
                        }
                    }
                } else {
                    //Log.d(log_tag, "OutputAvcBuffer, AVC NAL type: "+nal_type);
                }

                //2. configure AVC decoder with SPS/PPS
                if (sps_nal != null && pps_nal != null) {

                    int[] width = new int[1];
                    int[] height = new int[1];
                    AvcUtils.parseSPS(sps_nal, width, height);
                    mDecoder.configure(sps_nal, pps_nal,surfaceViewDecode.getHolder().getSurface());
                    mDecoder.start();
                    if (codecHandler != null) {
                        codecHandler.sendEmptyMessage(MSG_DECODE);
                    }
                }

            }
        }
    }

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_media_codec_test);
//    }
}

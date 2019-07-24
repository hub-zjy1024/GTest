package p.js.gtest.view.camera;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.common.GlobalHistogramBinarizer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;

/**
 Created by 张建宇 on 2019/7/17. */
public class ScanZxingContainer extends ScanViewContainer {
    MultiFormatReader multiFormatReader;
    Map<DecodeHintType, Object> hints;
    public static final int BARCODE_MODE = 0X100;
    public static final int QRCODE_MODE = 0X200;
    public static final int ALL_MODE = 0X300;
    public int decodeMode = ALL_MODE;

    public ScanZxingContainer(@NonNull Context context) {
        super(context);
    }

    public ScanZxingContainer(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ScanZxingContainer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public void init() {
        super.init();
        multiFormatReader = new MultiFormatReader();
        hints = new EnumMap<DecodeHintType, Object>(DecodeHintType.class);
        Collection<BarcodeFormat> decodeFormats = new ArrayList<BarcodeFormat>();
        decodeFormats.addAll(EnumSet.of(BarcodeFormat.AZTEC));
        decodeFormats.addAll(EnumSet.of(BarcodeFormat.PDF_417));
        switch (decodeMode) {
            case BARCODE_MODE:
                decodeFormats.add(BarcodeFormat.CODABAR);

                break;

            case QRCODE_MODE:
                decodeFormats.add(BarcodeFormat.QR_CODE);
                decodeFormats.add(BarcodeFormat.PDF_417);

                break;
            case ALL_MODE:
                break;
            default:
                break;
        }
        hints.put(DecodeHintType.POSSIBLE_FORMATS, decodeFormats);
        multiFormatReader.setHints(hints);
    }

    @Override
    protected String startDecode(byte[] data, Rect rect, int width, int height) {
        Result rawResult = null;
        try {
            PlanarYUVLuminanceSource source = buildLuminanceSource(data, rect, height, width);
            BinaryBitmap bitmap = new BinaryBitmap(new GlobalHistogramBinarizer(source));
            rawResult = multiFormatReader.decodeWithState(bitmap);
            return rawResult.getText();
        } catch (ReaderException re) {
            Log.e("zjy", getClass() + "->startDecode(): read Exception==" + re.getMessage());
            // continue
        } catch (RuntimeException re) {
            re.printStackTrace();
        } catch (Exception re) {
            re.printStackTrace();
            // continue
        } finally {
            multiFormatReader.reset();
        }
        return null;
    }

    private PlanarYUVLuminanceSource buildLuminanceSource(byte[] data, Rect rect, int height, int width) {
        return new PlanarYUVLuminanceSource(data, width, height, rect.left, rect.top, rect.width(), rect
                .height(), false);
    }

}

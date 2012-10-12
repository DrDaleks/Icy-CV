package plugins.adufour.javacv;

import static com.googlecode.javacv.cpp.opencv_highgui.cvGrabFrame;
import static com.googlecode.javacv.cpp.opencv_highgui.cvRetrieveFrame;
import icy.image.IcyBufferedImage;
import icy.math.FPSMeter;
import icy.sequence.Sequence;
import icy.sequence.SequenceUtil;
import icy.system.IcyHandledException;
import icy.system.SystemUtil;
import icy.system.thread.ThreadUtil;
import icy.type.DataType;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.ByteBuffer;

import javax.swing.Timer;

import plugins.adufour.ezplug.EzButton;
import plugins.adufour.ezplug.EzPlug;
import plugins.adufour.ezplug.EzVar;
import plugins.adufour.ezplug.EzVarEnum;
import plugins.adufour.ezplug.EzVarInteger;
import plugins.adufour.ezplug.EzVarListener;

import com.googlecode.javacv.cpp.opencv_core.IplImage;
import com.googlecode.javacv.cpp.opencv_highgui;
import com.googlecode.javacv.cpp.opencv_highgui.CvCapture;

public class WebcamCapture extends EzPlug
{
    private enum CaptureFormat
    {
        FORMAT_320x200, FORMAT_640x480,
    }
    
    private EzVarEnum<CaptureFormat> format   = new EzVarEnum<CaptureFormat>("Format", CaptureFormat.values());
    
    private EzVarInteger             interval = new EzVarInteger("capture interval", 100, 10, 10000, 1);
    
    private CvCapture                cc;
    
    private Timer                    timer;
    
    private Sequence                 sequence;
    
    private boolean                  firstRun = true;
    
    private FPSMeter                 fps      = new FPSMeter();
    
    @Override
    public void execute()
    {
        cc = opencv_highgui.cvCreateCameraCapture(0);
        
        if (cc == null) throw new IcyHandledException("No camera detected.");
        
        switch (format.getValue(true))
        {
            case FORMAT_320x200:
                opencv_highgui.cvSetCaptureProperty(cc, opencv_highgui.CV_CAP_PROP_FRAME_WIDTH, 320);
                opencv_highgui.cvSetCaptureProperty(cc, opencv_highgui.CV_CAP_PROP_FRAME_HEIGHT, 240);
            break;
            case FORMAT_640x480:
                opencv_highgui.cvSetCaptureProperty(cc, opencv_highgui.CV_CAP_PROP_FRAME_WIDTH, 640);
                opencv_highgui.cvSetCaptureProperty(cc, opencv_highgui.CV_CAP_PROP_FRAME_HEIGHT, 480);
            break;
            default:
                throw new IllegalArgumentException("Unsupported format: " + format.getValue());
        }
        
        if (SystemUtil.isMac() && firstRun)
        {
            int count = 0;
            while (count++ < 100 && cvGrabFrame(cc) != 0 && cvRetrieveFrame(cc) == null)
                ThreadUtil.sleep(100);
        }
        
        final int width = (int) opencv_highgui.cvGetCaptureProperty(cc, opencv_highgui.CV_CAP_PROP_FRAME_WIDTH);
        final int height = (int) opencv_highgui.cvGetCaptureProperty(cc, opencv_highgui.CV_CAP_PROP_FRAME_HEIGHT);
        
        final IcyBufferedImage icy = new IcyBufferedImage(width, height, 3, DataType.UBYTE);
        sequence = new Sequence(icy);
        addSequence(sequence);
        
        byte[][] data = icy.getDataXYCAsByte();
        final byte[] r = data[0];
        final byte[] g = data[1];
        final byte[] b = data[2];
        
        timer = new Timer(interval.getValue(), new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent arg0)
            {
                opencv_highgui.cvGrabFrame(cc);
                IplImage image = opencv_highgui.cvRetrieveFrame(cc, 0);
                if (image == null) return;
                
                ByteBuffer bb = image.getByteBuffer(0);
                int off = 0;
                for (int j = 0; j < height; j++)
                {
                    for (int i = 0; i < width; i++, off++)
                    {
                        b[off] = (byte) (bb.get() & 0xff);
                        g[off] = (byte) (bb.get() & 0xff);
                        r[off] = (byte) (bb.get() & 0xff);
                    }
                    bb.position(bb.position() + width);
                }
                icy.dataChanged();
                
                fps.update();
                getUI().setProgressBarMessage((int) fps.getRate() + " frames per sec.");
            }
        });
        
        timer.start();
    }
    
    @Override
    public void clean()
    {
        if (cc != null) opencv_highgui.cvReleaseCapture(cc);
    }
    
    @Override
    protected void initialize()
    {
        JavaCV.initialize();
        
        addEzComponent(format);
        addEzComponent(interval);
        interval.addVarChangeListener(new EzVarListener<Integer>()
        {
            @Override
            public void variableChanged(EzVar<Integer> source, Integer newValue)
            {
                if (timer != null) timer.setDelay(newValue);
            }
        });
        addEzComponent(new EzButton("Snapshot", new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if (sequence != null) addSequence(SequenceUtil.getCopy(sequence));
            }
        }));
        addEzComponent(new EzButton("Stop", new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if (timer != null) timer.stop();
                if (cc != null) opencv_highgui.cvReleaseCapture(cc);
            }
        }));
    }
}

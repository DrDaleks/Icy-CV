package plugins.adufour.javacv.osx;

import icy.system.SystemUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class OSXLoader
{
    private static volatile boolean initialized = false;
    
    public static void initialize()
    {
        if (!initialized)
        {
            if (!SystemUtil.isMac()) throw new UnsupportedOperationException("Warning: Mac OS X (Intel 64) only");
            
            initialized = true;
            
            try
            {
                String basePath = new File("").getAbsolutePath() + "/";
                
                ArrayList<String> libs = new ArrayList<String>();
                libs.add("libjniopencv_calib3d.dylib");
                libs.add("libjniopencv_core.dylib");
                libs.add("libjniopencv_features2d.dylib");
                libs.add("libjniopencv_flann.dylib");
                libs.add("libjniopencv_highgui.dylib");
                libs.add("libjniopencv_imgproc.dylib");
                libs.add("libjniopencv_legacy.dylib");
                libs.add("libjniopencv_ml.dylib");
                libs.add("libjniopencv_nonfree.dylib");
                libs.add("libjniopencv_objdetect.dylib");
                libs.add("libjniopencv_photo.dylib");
                libs.add("libjniopencv_stitching.dylib");
                libs.add("libjniopencv_video.dylib");
                libs.add("libjniopencv_videostab.dylib");
                libs.add("libjniopencv_contrib.dylib");
                for (String name : libs)
                {
                    System.out.print("loading " + name + "...");
                    InputStream stream = OSXLoader.class.getResourceAsStream(name);
                    File f = new File(basePath + name);
                    f.createNewFile();
                    FileOutputStream fo = new FileOutputStream(f);
                    byte[] data = new byte[stream.available()];
                    int n;
                    while ((n = stream.read(data)) > 0)
                        fo.write(data, 0, n);
                    fo.close();
                    System.load(f.getAbsolutePath());
                    f.deleteOnExit();
                    System.out.println("done.");
                }
            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
}

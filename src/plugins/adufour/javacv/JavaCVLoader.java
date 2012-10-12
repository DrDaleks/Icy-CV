package plugins.adufour.javacv;

import icy.plugin.PluginLoader;
import icy.system.SystemUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.xeustechnologies.jcl.JarClassLoader;

import plugins.adufour.javacv.osx.OSXLoader;

public class JavaCVLoader
{
    private static volatile boolean initialized = false;
    
    /**
     * Loads OpenCV libraries into the system. This method can be called several times, however only
     * the first call will actually lood the libraries
     */
    public static void initialize()
    {
        if (!initialized)
        {
            initialized = true;
            
            try
            {
                String basePath = new File("").getAbsolutePath() + "/";
                
                ArrayList<String> libs = new ArrayList<String>();
                libs.add("libopencv_core.2.4.dylib");
                libs.add("libopencv_imgproc.2.4.dylib");
                libs.add("libopencv_highgui.2.4.dylib");
                libs.add("libopencv_flann.2.4.dylib");
                libs.add("libopencv_features2d.2.4.dylib");
                libs.add("libopencv_ml.2.4.dylib");
                libs.add("libopencv_nonfree.2.4.dylib");
                libs.add("libopencv_objdetect.2.4.dylib");
                libs.add("libopencv_photo.2.4.dylib");
                libs.add("libopencv_video.2.4.dylib");
                libs.add("libopencv_calib3d.2.4.dylib");
                libs.add("libopencv_gpu.2.4.dylib");
                libs.add("libopencv_stitching.2.4.dylib");
                libs.add("libopencv_legacy.2.4.dylib");
                libs.add("libopencv_videostab.2.4.dylib");
                libs.add("libopencv_contrib.2.4.dylib");
                for (String name : libs)
                {
                    InputStream stream = JavaCVLoader.class.getResourceAsStream(name);
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
            
            ClassLoader cl = PluginLoader.getLoader();
            
            if (cl instanceof JarClassLoader)
            {
                JarClassLoader jcl = (JarClassLoader) cl;
                InputStream javacv = JavaCVLoader.class.getResourceAsStream("javacv.jar");
                jcl.add(javacv);
                InputStream javacpp = JavaCVLoader.class.getResourceAsStream("javacpp.jar");
                jcl.add(javacpp);
            }
            
            if (SystemUtil.isMac()) OSXLoader.initialize();
        }
    }
}

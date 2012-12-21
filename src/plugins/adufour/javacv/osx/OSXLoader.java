package plugins.adufour.javacv.osx;

import icy.plugin.PluginLoader;
import icy.system.IcyHandledException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLClassLoader;
import java.util.ArrayList;

import org.xeustechnologies.jcl.JarClassLoader;

public class OSXLoader
{
    private static volatile boolean initialized = false;
    
    public static void initialize()
    {
        if (!initialized)
        {
            ClassLoader cl = PluginLoader.getLoader();
            
            if (cl instanceof JarClassLoader)
            {
                JarClassLoader jcl = (JarClassLoader) cl;
                System.out.print("Loading JavaCV v.2.4.2...");
                InputStream javacv = OSXLoader.class.getResourceAsStream("javacv_osx_2.4.2.jar");
                jcl.add(javacv);
                System.out.println("done.");
            }
            else if (cl instanceof URLClassLoader)
            {
                // Probably running from an IDE, hopefully JavaCPP is already in the classpath
            }
            else
            {
                throw new IcyHandledException("Unable to load JavaCV and JavaCPP into the class loader");
            }
            
            try
            {
                String basePath = new File("").getAbsolutePath() + "/";
                
                ArrayList<String> libs = new ArrayList<String>();
                
                // WARNING ! Loading must be done IN ORDER
                
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
                    InputStream stream = OSXLoader.class.getResourceAsStream(name.replace("2.4", "2.4.2"));
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
                
                initialized = true;
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

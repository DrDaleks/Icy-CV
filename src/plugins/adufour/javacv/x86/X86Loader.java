package plugins.adufour.javacv.x86;

import icy.plugin.PluginLoader;
import icy.plugin.classloader.JarClassLoader;
import icy.system.IcyHandledException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLClassLoader;
import java.util.ArrayList;

public class X86Loader
{
	private static volatile boolean initialized = false;

	public static void initialize()
	{
		if (!initialized)
		{
			initialized = true;

			ClassLoader cl = PluginLoader.getLoader();

			if (cl instanceof JarClassLoader)
			{
				JarClassLoader jcl = (JarClassLoader) cl;
				System.out.print("Loading JavaCV v.2.4.3...");
				jcl.add(X86Loader.class.getResource("javacv_x86_2.4.3.jar"));
				System.out.println("done.");
			}
			else if (cl instanceof URLClassLoader)
			{
				// Probably running from an IDE, hopefully JavaCPP is already in the classpath
			}
			else
			{
				throw new IcyHandledException("Unable to load JavaCV v.2.4.3 into the class loader");
			}

			try
			{
				 String basePath = new File("").getAbsolutePath() + "/";

				// WARNING ! Loading must be done IN ORDER

				ArrayList<String> libs = new ArrayList<String>();
				libs.add("libstdc++-6.dll");
				libs.add("libgcc_s_dw2-1.dll");
				libs.add("opencv_core243.dll");
				libs.add("opencv_imgproc243.dll");
				libs.add("opencv_highgui243.dll");
				libs.add("opencv_flann243.dll");
				libs.add("opencv_features2d243.dll");
				libs.add("opencv_ml243.dll");
				libs.add("opencv_nonfree243.dll");
				libs.add("opencv_objdetect243.dll");
				libs.add("opencv_photo243.dll");
				libs.add("opencv_video243.dll");
				libs.add("opencv_calib3d243.dll");
				libs.add("opencv_gpu243.dll");
				libs.add("opencv_stitching243.dll");
				libs.add("opencv_legacy243.dll");
				libs.add("opencv_videostab243.dll");
				// libs.add("opencv_ts243.dll");
				// libs.add("opencv_ffmpeg243.dll");
				libs.add("opencv_contrib243.dll");
				libs.add("jniopencv_core.dll");
				libs.add("jniopencv_imgproc.dll");
				libs.add("jniopencv_highgui.dll");
				libs.add("jniopencv_flann.dll");
				libs.add("jniopencv_features2d.dll");
				libs.add("jniopencv_ml.dll");
				libs.add("jniopencv_nonfree.dll");
				libs.add("jniopencv_objdetect.dll");
				libs.add("jniopencv_photo.dll");
				libs.add("jniopencv_video.dll");
				libs.add("jniopencv_calib3d.dll");
				libs.add("jniopencv_stitching.dll");
				libs.add("jniopencv_legacy.dll");
				libs.add("jniopencv_videostab.dll");
				libs.add("jniopencv_contrib.dll");
				for (String name : libs)
				{
					System.out.print("Loading " + name + "...");
					 System.out.print("Installing " + name + "...");
					 InputStream stream = X86Loader.class.getResourceAsStream(name);
					 File f = new File(basePath + name);
					 f.createNewFile();
					 FileOutputStream fo = new FileOutputStream(f);
					 byte[] data = new byte[stream.available()];
					 int n;
					 while ((n = stream.read(data)) > 0)
					 fo.write(data, 0, n);
					 fo.close();
					 f.deleteOnExit();
					  System.load(f.getAbsolutePath());
					 System.out.println("done.");

//					((JarClassLoader) cl).add(X86Loader.class.getResource(name.replace("2.4", "2.4.3")));
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
			catch (UnsatisfiedLinkError link)
			{
				String s = "Couldn't load OpenCV libraries.\n";
				s += "Please make sure the Microsoft C/C++ Runtimes are installed\n";
				s += "and that Icy is in a folder with read/write access.";
				throw new IcyHandledException(s, link);
			}
		}
	}

}

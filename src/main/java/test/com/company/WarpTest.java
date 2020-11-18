package test.com.company; 

import com.company.ConcurAfflineS;
import com.company.Operator;
import com.company.Warp;
import org.junit.Test;
import org.junit.Before; 
import org.junit.After;
import org.opencv.core.Mat;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;


public class WarpTest { 




@Test
public void testWarp() throws Exception { 
//TODO: Test goes here...
    long start,end;
    System.load("D:\\Software\\CodingRelate\\opencv-4.4.0-windows\\opencv\\build\\java\\x64\\opencv_java440.dll");

    Mat img = Imgcodecs.imread("D:\\Programming\\JAVA\\MLSDeformation\\src\\main\\java\\OIP.jpg");
    float center[]  = {102,316};
    float orient[]  = {276,278};

//    Mat img = Imgcodecs.imread("D:\\Programming\\JAVA\\MLSDeformation\\src\\main\\java\\girl.jpg");

    int height =  img.rows();
    int width  =  img.cols();
    Operator operator = new Operator(img);
    start = System.currentTimeMillis();
    Warp.warp(center,orient,height,width,operator);
    end = System.currentTimeMillis();
    operator.saveChange();
    Mat deformImg = (Mat)operator.getDeformImg();
    HighGui.namedWindow("image", HighGui.WINDOW_AUTOSIZE);
    HighGui.imshow("image",deformImg );
    HighGui.waitKey();
    System.out.printf("Use time is %d",end-start);


} 


} 

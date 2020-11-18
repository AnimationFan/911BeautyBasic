package test.com.company; 

import com.company.Affline;
import com.company.AfflineS;
import com.company.ConcurAfflineS;
import com.company.Operator;
import org.junit.Test;
import org.junit.Before; 
import org.junit.After;
import org.opencv.core.Mat;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;


public class ConcurAfflineSTest { 


@Test
public void testChangeImage() throws Exception {
    long start,end;
    System.load("D:\\Software\\CodingRelate\\opencv-4.4.0-windows\\opencv\\build\\java\\x64\\opencv_java440.dll");

//    Mat img = Imgcodecs.imread("D:\\Programming\\JAVA\\MLSDeformation\\src\\main\\java\\OIP.jpg");
//    int [][]p=new int[][]{{180, 288}, {337,288}};
//    int [][]q=new int[][]{{205, 294}, {316, 288}};

    Mat img = Imgcodecs.imread("D:\\Programming\\JAVA\\MLSDeformation\\src\\main\\java\\girl.jpg");
    int [][]q = new int[][]{{515, 509},{546, 506}};
    int [][]p = new int[][]{{484, 509},{591, 501}};

    int height =  img.rows();
    int width  =  img.cols();
    Operator operator = new Operator(img);
    ConcurAfflineS affline = new ConcurAfflineS(p,q,height,width,operator);
    start = System.currentTimeMillis();
    affline.changeImage();
    end = System.currentTimeMillis();
    Mat deformImg = (Mat)affline.getDeformImg();
    HighGui.namedWindow("image", HighGui.WINDOW_AUTOSIZE);
    HighGui.imshow("image",deformImg );
    HighGui.waitKey();
    System.out.printf("Use time is %d",end-start);
} 


} 

package com.company;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.CvType.*;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.awt.*;

import static org.opencv.core.CvType.*;


public class Main {

    public static void showPicture(){
        Mat img = Imgcodecs.imread("D:\\Programming\\JAVA\\MLSDeformation\\src\\girl.jpg");

        Mat grey = new Mat();
        Imgproc.cvtColor(img, grey, Imgproc.COLOR_BGR2GRAY);
        Mat sobelx = new Mat();
        Imgproc.Sobel(grey, sobelx, CvType.CV_32F, 1, 0);
        Core.MinMaxLocResult res = Core.minMaxLoc(sobelx); // find minimum and maximum intensities
        Mat draw = new Mat();
        double maxVal = res.maxVal, minVal = res.minVal;
        sobelx.convertTo(draw, CvType.CV_8U, 255.0 / (maxVal - minVal), -minVal * 255.0 / (maxVal - minVal));
        HighGui.namedWindow("image", HighGui.WINDOW_AUTOSIZE);
        HighGui.imshow("image", img);
        HighGui.waitKey();
    }

    //注对于高维数组，只能使用该方式访问
    public static void test_get(){
        Mat mat = new Mat(new int[]{3,3,3},CvType.CV_32S);
        mat.put(new int[]{0,0,0},new int[]{3,3,3});
        int []m = new int[27];
        System.out.println(m[0]);
        int []loc = new int[]{0,0,0};
        mat.get(loc,m);
        System.out.println(m[0]);
        return;
    }

    public static void test_divide(){
        Mat mat = new Mat(new int[]{3,3,3,3},CvType.CV_32SC1);
        mat.put(new int[]{0,0,0,0},new int[]{2,2,2});
        mat.convertTo(mat,CV_32F);
        Core.divide(1.0,mat,mat);
        float m[] = new float[3];
        mat.get(new int[]{0,0,0,0},m);
        return ;
    }

    public static void test_substract(){
        Mat mat = new Mat();
        mat = Mat.ones(new int[]{3,3,3},CV_32F);
        Mat mat2= new Mat();
        mat2 = Mat.ones(new int[]{3,3,3},CV_32F);
        Core.multiply(mat,mat,mat,3.0);
        float[] m = new float[3];
        mat.get(new int[]{0,0,0},m);
        Core.subtract(mat,mat2,mat2);
        mat2.get(new int[]{0,0,0},m);
        System.out.println(m);
        return;
    }

    public static void test_multiply(){
        Mat mat = new Mat();
        mat = Mat.ones(new int[]{3,3,3},CV_32F);
        Core.multiply(Mat.ones(new int[]{3,3,3},CV_32F),mat,mat,-3.0);
        float[] m =new float[3];
        mat.get(new int[]{0,0,0},m);
        return;
    }


    public static void main(String[] args) {
        System.load("D:\\Software\\CodingRelate\\opencv-4.4.0-windows\\opencv\\build\\java\\x64\\opencv_java440.dll");
        Mat img = Imgcodecs.imread("D:\\Programming\\JAVA\\MLSDeformation\\src\\main\\java\\girl.jpg");
        Mat deformimg = test_point_affline_img(img);
        HighGui.namedWindow("image", HighGui.WINDOW_AUTOSIZE);
        HighGui.imshow("image",deformimg );
        HighGui.waitKey();
    }

    public static void test_single_point(int[]v){
        float [][]q = new float[][]{{515, 509},{546, 506}};
        float [][]p = new float[][]{{484, 509},{591, 501}};
        int height =  1080;
        int width  =  1080;
        int[] point = point_affline(v,p,q,height,width);
        System.out.printf("(%d,%d)",point[0],point[1]);
    }

    public static void test_point_affline(){

        float [][]q = new float[][]{{515, 509},{546, 506}};
        float [][]p = new float[][]{{484, 509},{591, 501}};
        int height =  1080;
        int width  =  1080;
        int x=0;
        for(int i=0; i<height;i+=1){
            for (int j =0; j<width;j+=1){
                if(i==1079&& j==1079)
                    x++;
                int[] point = point_affline(new int[]{i,j},p,q,height,width);
                System.out.printf("(%d,%d)\t\t(%d,%d)\n",i,j,point[0],point[1]);

            }
        }


    }


    public static Mat test_point_affline_img(Mat img){
        Mat result = img.clone();
        byte[] img_byte = new byte[img.height()*img.rows()*3];
        byte[] result_byte = new byte[img.height()*img.rows()*3];
        img.get(0,0,img_byte);

        float [][]q = new float[][]{{515, 509},{546, 506},{605,552}};
        float [][]p = new float[][]{{484, 509},{591, 501},{565,541}};
        byte[] color =new byte[3];
        int height =  img.height();
        int width  =  img.width();
        for(int i=0; i<height;i+=1){
            for (int j =0; j<width;j+=1){
                int[] point = point_affline(new int[]{i,j},p,q,height,width);
                //System.out.printf("(%d,%d)\n",point[0],point[1]);
                result_byte[i*width*3+j*3+0] = img_byte[point[0]*width*3+point[1]*3+0];
                result_byte[i*width*3+j*3+1] = img_byte[point[0]*width*3+point[1]*3+1];
                result_byte[i*width*3+j*3+2] = img_byte[point[0]*width*3+point[1]*3+2];
            }
            if(i%100 ==0)
                System.out.println(i);
        }
        result.put(new int[]{0,0},result_byte);
        return result;
    }

    public static int[] point_affline(int []v, float[][]p_, float[][]q_, int height,int width){
        //坐标点预处理，建议直接改由预处理完成
        //不能直接对原始数据操作
        float[][] p = p_.clone();
        for(int i=0; i< p.length;i++)
            p[i] = p_[i].clone();
        float[][] q = q_.clone();
        for(int i=0; i<q.length;i++)
            q[i] =q_[i].clone();

        float [][] m = q;
        q = p; p = m;

        int ctrl = p.length;
        //前处理，应当放置于矩阵之外
        for(int i = 0; i<ctrl;i++){
            float n_0 = p[i][0];
            p[i][0] = p[i][1];
            p[i][1] = n_0;

            float n_1 = q[i][0];
            q[i][0] = q[i][1];
            q[i][1] = n_1;
        }

        float [][] temp = new float[ctrl][2];
        for (int i= 0; i<ctrl;i++){
            for (int j = 0; j<2;j++){
                temp[i][j] = p[i][j] - v[j];
                temp[i][j] = temp[i][j]*temp[i][j];
            }

        }

        //计算w
        float[] w = new float[ctrl];            //[ctrl]
        float sum_w =0;
        {
            for (int i = 0; i < ctrl; i++){
                w[i] = 1/(temp[i][0]+temp[i][1]);
            }
            for (int i = 0; i < ctrl; i++){
                sum_w += w[i];
            }
        }
        //计算p_start
        float[] pstar = new float[2];                      //[2]
        float[][] phat = new float[ctrl][2];
        {
            for(int i =0; i<ctrl; i++){
                pstar[0] += p[i][0]*w[i];
                pstar[1] += p[i][1]*w[i];
            }
            pstar[0] =pstar[0]/sum_w;
            pstar[1] =pstar[1]/sum_w;
            for (int i =0;i<ctrl;i++){
                phat[i][0] = p[i][0]-pstar[0];
                phat[i][1] = p[i][1]-pstar[1];
            }
        }
        float[][] neg_phat_verti = new float[ctrl][2];      //[ctrl][2]
        {
            for (int i=0;i<ctrl;i++){
                neg_phat_verti[i][0] =phat[i][1];
                neg_phat_verti[i][1] =-phat[i][0];
            }
        }
        float[][][] mul_left = new float[ctrl][2][2];       //[ctrl][2][2]
        for(int i=0; i< ctrl;i++){
            mul_left[i][0] = phat[i];
            mul_left[i][1] = neg_phat_verti[i];

        }

        //v-p*
        float[] vpstar = new float[2];
        float[] neg_vpstar_verti = new float[2];
        {
            //因为太短这里就不写循环了
            vpstar[0] = v[0]-pstar[0];
            vpstar[1] = v[1]-pstar[1];
            neg_vpstar_verti[0] = vpstar[1];
            neg_vpstar_verti[1] = -vpstar[0];
        }
        float[][] mul_right = new float[2][2];
        {
            //因为元素比较少，所以直接选择赋值了
            mul_right[0][0]=vpstar[0];
            mul_right[0][1]=neg_vpstar_verti[0];
            mul_right[1][0]=vpstar[1];
            mul_right[1][1]=neg_vpstar_verti[1];
        }

        //仿射矩阵A
        float[][][] A =new float[ctrl][2][2];
        {
            //矩阵运算，不知道能用什么高速函数替代
            for(int i=0; i<ctrl; i++){
                A[i][0][0]=w[i]*(mul_left[i][0][0]*mul_right[0][0]+mul_left[i][0][1]*mul_right[1][0]) ;
                A[i][0][1]=w[i]*(mul_left[i][0][0]*mul_right[0][1]+mul_left[i][0][1]*mul_right[1][1])  ;
                A[i][1][0]=w[i]*(mul_left[i][1][0]*mul_right[0][0]+mul_left[i][1][1]*mul_right[1][0])  ;
                A[i][1][1]=w[i]*(mul_left[i][1][0]*mul_right[0][1]+mul_left[i][1][1]*mul_right[1][1])  ;
            }
        }

        //q
        float[] qstar = new float[2];               //[2]
        float[][] qhat = new float[ctrl][2];        //[ctrl,2]
        {
            for(int i =0; i < ctrl; i++){
                qstar[0] +=w[i]*q[i][0];
                qstar[1] +=w[i]*q[i][1];
            }
            qstar[0] = qstar[0]/sum_w;
            qstar[1] = qstar[1]/sum_w;
            for(int i=0; i<ctrl;i++){
                qhat[i][0]=q[i][0] - qstar[0];
                qhat[i][1]=q[i][1] - qstar[1];
            }
        }

        //qhat与A作矩阵运算[是多组矩阵叠加，不知如何优化]
        float[][] qhat_A =new float[ctrl][2];
        float[] qhat_A_0 = new float[2];
        for(int i=0; i<ctrl;i++){
            qhat_A[i][0] = qhat[i][0]*A[i][0][0] +qhat[i][1]*A[i][1][0];
            qhat_A[i][1] = qhat[i][0]*A[i][0][1] +qhat[i][1]*A[i][1][1];
            qhat_A_0[0] +=qhat_A[i][0];
            qhat_A_0[1] +=qhat_A[i][1];
        }
        //对qhat_A_0求范数
        float norm_qhat_A = (float) Math.sqrt(qhat_A_0[0]*qhat_A_0[0] + qhat_A_0[1]*qhat_A_0[1]);
        float nor_vpstar = (float)Math.sqrt(vpstar[0]*vpstar[0]+vpstar[1]*vpstar[1]);
        int[] transfer = new int[2];
        transfer[0] = (int)(qhat_A_0[0]*nor_vpstar/norm_qhat_A+qstar[0]);
        transfer[1] = (int)(qhat_A_0[1]*nor_vpstar/norm_qhat_A+qstar[1]);

        //阶段
        if(transfer[0]<0 || transfer[0] > height-1)
            transfer[0] =0;
        if(transfer[1]<0 || transfer[1] >width-1)
            transfer[1] =0;

        return transfer;

    }

    public static Mat transfer(Mat image,int [][] p,int [][] q){
        //java 不支持默认参数，因此只能之后重载参数了
        //变形参数，destiny为取点间距
        float alpha = 1;
        float destiny = 1;
        //默认为 RGB格式的Mat

        int height = image.rows();
        int width  = image.cols();
        Mat mat = new Mat();

        int ctrl = p.length;
        //交换p,q的值
        {
            int[][] m = p;
            p = q;
            q = m;
            //交换像素的值
            for (int i = 0; i < p.length; i++) {
                int t = p[i][0];
                p[i][0] = p[i][1];
                p[i][1] = p[i][0];

                t = q[i][0];
                q[i][0] = q[i][1];
                q[i][1] = q[i][0];
            }
        }


        //构建lineSpacee
        //因为是demo 暂不考虑大间距的情况，因此这里暂不使用linesapce
        //构建linespace
        //生成坐标矩阵
        int[] vx = new int[height*width];
        int[] vy = new int[height*width];

        for(int  i= 0 ; i< height;i++){
            for(int j = 0 ; j <width;j++){
                vx[i*height+j] = j;
                vy[i*height+j] = i;
            }
        }

        //生成v,p 并计算w

        //定义p,v
        int size[] =new int []{p.length,2,height,width};
        Mat reshape_p = new Mat(size, CV_32SC1);    //[ctrls, 2, height, width]
        Mat reshape_v = new Mat(size, CV_32SC1);    //[ctrls, 2, height, width]

        //p,v赋值
        {
            short m []  = new short[height * width];
            //reshape_p 赋值
            for(int i = 0; i < p.length ;i++){
                for(int j = 0; j <2 ;j++){
                    //m 赋值为 p[i][j]
                    for(int  count_i =0 ; count_i <height*width;count_i++){
                        m[count_i] = (short)p[i][j];
                    }
                    int[] loc = new int[]{i,j,0,0};
                    reshape_p.put(loc,m);
                }
            }
            //reshape_m 赋值
            for(int i = 0; i < p.length; i++){
                for(int j = 0 ; j < 2;j++){
                    if(j ==0)
                        reshape_v.put(new int[]{i,j,0,0},vx);
                    if(j == 1)
                        reshape_v.put(new int[]{i,j,0,0},vy);
                }
            }


        }
        //计算w
        Mat w = new Mat(size,CV_32SC1);          //w [ctrls,height,width]
        {
            Core.subtract(reshape_p,reshape_v,w);
            w.mul(w);                                   //[ctrls,2,height,width];
            Mat w1 = new Mat(new int[]{p.length,height,width},CV_32SC1);  //[ctrls,height,width]
            //沿w的1维叠加
            for(int i =0 ;i < p.length ; i++){
                int m[] =new int[height*width],n []=new int[height*width];
                w.get(new int[]{i,0,0,0},m);
                w.get(new int[]{i,1,0,0},n);
                Mat M = new Mat(new int[]{height,width},CV_32SC1);
                M.put(new int[]{0,0},m);
                Mat N = new Mat(new int[]{height,width},CV_32SC1);
                N.put(new int[]{0,0},n);
                Core.add(M,N,M);
                M.get(new int[]{0,0},m);
                w1.put(i,0,m);
            }
            w1.convertTo(w1,CV_32F);
            Core.divide(1.0,w1,w1);
            w = w1;             //w [ctrls,height,width] ,可以用reduce改写
        }

        //计算sum_w
        Mat sum_w = new Mat();
        sum_w= Mat.zeros(new int[]{height,width},CV_32F);   //[height,width]
        {
            Mat temp = new Mat(new int[]{height,width},CV_32F);
            float[] m = new float[height*width];
            for(int i=0; i<p.length;i++){
                w.get(new int[]{i,0,0},m);
                temp.put(new int[]{0,0},m);
                Core.add(sum_w,temp,sum_w);
            }
        }

        //计算p_start
        Mat p_start =new Mat();
        p_start= Mat.zeros(new int[]{2,height,width},CV_32F);
        {
            int [][] reshape_p_transpose = new int[2][ctrl];
            for(int i = 0; i<ctrl;i++){
                for(int j =0; j<2;j++){
                    reshape_p_transpose[j][i] = p[i][j];
                }
            }
            //w*reshape_p_transpose

            float[] n = new float[height*width];
            for(int i=0; i< 2;i++){
                Mat sum = new Mat(new int[]{height,width},CV_32F);
                Mat one = new Mat(new int[]{height,width},CV_32F);
                for(int j =0; j<ctrl;j++){

                    w.get(new int[]{j,0,0},n);  //n[1,height,width]
                    Mat temp = new Mat(new int[]{height,width},CV_32F);
                    temp.put(new int[]{0,0},n);
                    Core.multiply(temp,one,temp,(double) p[i][j]);//
                    Core.add(temp,sum,sum);
                }

                Core.divide(sum,sum_w,sum);
                sum.get(new int[]{0,0},n);
                p_start.put(new int[]{i,0,0},n);

            }//push_back 是否有用存疑 , [2,height,col]



        }

        //计算P_hat
        Mat p_hat =new Mat(new int[]{ctrl,2,height,width},CV_32F);  //[ctrl,2,height,width]
        {
            float[] m = new float[height*width];
            for(int i = 0 ; i<ctrl;i++){
                Mat temp = new Mat();
                for(int j =0 ; j <2;j++){
                    temp = Mat.ones(new int[]{height,width},CV_32F);
                    Core.multiply(temp,temp,temp,p[i][j]);
                    p_start.get(new int[]{j,0,0},m);
                    Mat n = new Mat(new int[]{height,width},CV_32F);
                    n.put(new int[]{0,0},m);
                    Core.subtract(temp,n,temp);
                    temp.get(new int[]{0,0},m);
                    p_hat.put(new int[]{i,j,0,0},m);
                }

            }

        }


        //计算neg_phat_verti
        Mat neg_p_hat_verti = new Mat(new int[]{ctrl,2,height,width},CV_32F);
        {
            float[] m = new float[height*width];
            for(int i=0;i<ctrl;i++){
                //取等值
                p_hat.get(new int[]{i,1,0,0},m);
                neg_p_hat_verti.put(new int[]{i,0,0,0},m);
                //取反
                p_hat.get(new int[]{i,0,0,0},m);
                Mat n = new Mat(new int[]{height,width},CV_32F);
                n.put(new int[]{0,0},m);
                Mat one = new Mat();
                one = Mat.ones(new int[]{height,width},CV_32F);
                Core.multiply(one,n,n,-1.0);
                n.get(new int[]{0,0},m);
                neg_p_hat_verti.put(new int[]{i,1,0,0},m);
            }
        }

        //计算mul_left
        Mat mul_left =new Mat(new int[]{ctrl,2,2,height,width},CV_32F);
        {
            for(int i=0;i<ctrl;i++){
                float[] m = new float[2*height*width];
                p_hat.get(new int[]{i,0,0,0},m);
                mul_left.put(new int[]{i,0,0,0,0},m);
                neg_p_hat_verti.get(new int[]{i,0,0,0},m);
                mul_left.put(new int[]{i,1,0,0,0},m);
            }
        }

        //计算vpstart 即：v-(p*)
        Mat vpstart = new Mat(new int[]{2,height,width},CV_32F);
        {
            //生成reshape_v,由于最初是设为整型，所以需要一步符号类型的转换
            Mat r_v =new Mat(new int[]{2,height,width},CV_32SC1);
            r_v.put(new int[]{0,0,0},vx);
            r_v.put(new int[]{1,0,0},vy);
            r_v.convertTo(r_v,CV_32F);
            Core.subtract(r_v,p_start,vpstart);
        }

        //计算neg_vpstart_verti 即
        Mat neg_vpstart_verti = new Mat(new int[]{2,height,width},CV_32F);
        {
         float[] m = new float[height*width];
         vpstart.get(new int[]{1,0,0},m);
         neg_vpstart_verti.put(new int[]{0,0,0},m);


         vpstart.get(new int[]{0,0,0},m);
         //m
         Mat n = new Mat(new int[]{height,width},CV_32F);
         n.put(new int[]{0,0},m);
         Core.multiply(Mat.ones(new int[]{height,width},CV_32F),n,n,-1.0);
         neg_vpstart_verti.put(new int[]{1,0,0},m);

        }

        //计算mul_right,即堆叠vpstart,neg_vpstart_verti 为[2,2(新增)，height,width]
        Mat mul_right =new Mat(new int[]{2,2,height,width},CV_32F);
        {
            float[] m = new float[height*width];
            for(int i =0;i<2;i++){
                vpstart.get(new int[]{i,0,0},m);
                mul_right.put(new int[]{i,0,0,0},m);
                vpstart.put(new int[]{i,0,0},m);
                mul_right.put(new int[]{i,1,0,0},m);
            }
        }

        //计算矩阵A
        Mat A = new Mat();
        {
            Mat left = new Mat(new int[]{ctrl,2,2,height,width},CV_32F);
            //w       [ctrl,  , heigh, width]
            //mul_left[ctrl,2,2,height,width];
            for(int i = 0; i<ctrl;i++){
                float [] m = new float[height*width];
                float [] n = new float[height*width];
                w.get(new int[]{i,0,0},m);
                Mat mMat = new Mat(new int[]{height,width},CV_32F);
                mMat.put(new int[]{0,0},m);
                for(int j = 0; j<2;j++) {
                    for(int k=0;k<2;k++){
                        mul_left.get(new int[]{i,j,k,0,0},n);
                        Mat nMat = new Mat(new int[]{height,width},CV_32F);
                        nMat.put(new int[]{0,0},n);
                        Core.multiply(mMat,nMat,nMat);
                        nMat.get(new int[]{0,0},n);
                        left.put(new int[]{i,j,k,0,0},n);
                    }
                }
            }

            //left [ctrl ,2,2,height,width]
            //right[     ,2,2,height,width]
            //先将两个二维的部分转到末尾，在对最后2维做矩阵的点乘


            //改为直接在1，2维范围做矩阵运算
            //A    [ctrl height,width,2,2]
            float[] m = new float[height*width*4];
            float[] n = new float[height*width*4];
            mul_right.get(new int[]{0,0,0,0},n);
            float[] reuslt = new float[height*width*4];
            for( int i = 0; i<ctrl;i++){
                left.get(new int[]{i,0,0,0,0},m);
                for(int j =0; j<height;j++){
                    for (int k =0; k<width;k++){
                        reuslt[(0*2+0)*height*width+i*height+j]=m[(0*2+0)*height*width+i*height+j]*n[(0*2+0)*height*width+i*height+j]+m[(0*2+1)*height*width+i*height+j]*n[(1*2+0)*height*width+i*height+j];
                        reuslt[(0*2+1)*height*width+i*height+j]=m[(0*2+0)*height*width+i*height+j]*n[(0*2+1)*height*width+i*height+j]+m[(0*2+1)*height*width+i*height+j]*n[(1*2+1)*height*width+i*height+j];
                        reuslt[(1*2+0)*height*width+i*height+j]=m[(1*2+0)*height*width+i*height+j]*n[(0*2+0)*height*width+i*height+j]+m[(1*2+1)*height*width+i*height+j]*n[(1*2+0)*height*width+i*height+j];
                        reuslt[(1*2+1)*height*width+i*height+j]=m[(1*2+0)*height*width+i*height+j]*n[(0*2+1)*height*width+i*height+j]+m[(1*2+1)*height*width+i*height+j]*n[(1*2+1)*height*width+i*height+j];
                    }
                }
                left.put(new int[]{i,0,0,0,0},reuslt);
            }
            A=left;
        }

        return null;
    }
}

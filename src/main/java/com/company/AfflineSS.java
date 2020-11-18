package com.company;

public class AfflineSS extends Affline {
    private float w[];
    private float pstar[];
    private float mul_left[][][];
    private float vpstar[];
    private float mul_right[][];
    private float qstar[];
    private float qhat[][];
    float[][] qhat_A;
    float[] qhat_A_0;
    float [][] A ;
    int ctrl;
    public AfflineSS(int[][] p, int[][] q, int height, int width, ImageOperator operator) {
        super(p, q, height, width, operator);
         ctrl = p.length;
        w = new float[20];
        pstar = new float[2];
        mul_left = new float[20][2][2];
        vpstar = new float[2];
        mul_right = new float[2][2];
        qstar  = new float[2];
        qhat = new float[20][2];
        qhat_A =new float[20][2];
        qhat_A_0 = new float[2];
        A = new float[2][2];
    }

    @Override
    public int[] afflinePoint(int[] v) {

        //前处理，应当放置于矩阵之外
        //计算w
        //计算p_start

        pstar[0] =0; pstar[1]=0;
        qhat_A_0[0] =0;
        qhat_A_0[1] =0;


        //qhat_A_0 = new float[2];


        float sum_w =0;//[2]
        {
            for(int i =0; i<ctrl; i++){
                w[i]  = 1/((this.p[i][0] - v[0])*(this.p[i][0] - v[0])+(this.p[i][1] - v[1])*(this.p[i][1] - v[1]));
                pstar[0] += p[i][0]*w[i];
                pstar[1] += p[i][1]*w[i];
                sum_w+=w[i];
            }
            pstar[0] =pstar[0]/sum_w;
            pstar[1] =pstar[1]/sum_w;

        }
               //[ctrl][2][2]
        for(int i=0; i< ctrl;i++){
            mul_left[i][0][0] = p[i][0] - pstar[0] ;
            mul_left[i][0][1] = p[i][1] - pstar[1];
            mul_left[i][1][0] = p[i][1] - pstar[1];
            mul_left[i][1][1] = -p[i][0] + pstar[0];
        }

        //v-p*

        //因为太短这里就不写循环了
        vpstar[0] = v[0]-pstar[0];
        vpstar[1] = v[1]-pstar[1];

        {
            //因为元素比较少，所以直接选择赋值了
            mul_right[0][0]=vpstar[0];
            mul_right[0][1]=vpstar[1];
            mul_right[1][0]=vpstar[1];
            mul_right[1][1]=-vpstar[0];
        }
        //q
        //[ctrl,2]
        {
            qstar[0] =0;
            qstar[1] =0;
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

        for(int i=0; i<ctrl;i++){
            A[0][0]=w[i]*(mul_left[i][0][0]*mul_right[0][0]+mul_left[i][0][1]*mul_right[1][0]) ;
            A[0][1]=w[i]*(mul_left[i][0][0]*mul_right[0][1]+mul_left[i][0][1]*mul_right[1][1])  ;
            A[1][0]=w[i]*(mul_left[i][1][0]*mul_right[0][0]+mul_left[i][1][1]*mul_right[1][0])  ;
            A[1][1]=w[i]*(mul_left[i][1][0]*mul_right[0][1]+mul_left[i][1][1]*mul_right[1][1])  ;
            qhat_A[i][0] = qhat[i][0]*A[0][0] +qhat[i][1]*A[1][0];
            qhat_A[i][1] = qhat[i][0]*A[0][1] +qhat[i][1]*A[1][1];
            qhat_A_0[0] +=qhat_A[i][0];
            qhat_A_0[1] +=qhat_A[i][1];
        }
        //对qhat_A_0求范数
        float norm_qhat_A = (float)Math.sqrt(qhat_A_0[0]*qhat_A_0[0] + qhat_A_0[1]*qhat_A_0[1]);
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
}

package com.company;

public class Affline {
    public float[][] p;
    public float[][] q;
    public int height;
    public int width;
    public ImageOperator operator;

    public Affline(int[][] p, int[][] q,int height,int width,ImageOperator operator){
        this.width =width;
        this.height = height;
        this.operator = operator;

        //翻转
        if(q.length != p.length)
            return;
        else{
            this.p = new float[p.length][2];
            this.q = new float[q.length][2];
            for(int i = 0;i < p.length;i++ ){
                this.p[i][0]= q[i][1];
                this.p[i][1]= q[i][0];
                this.q[i][0]= p[i][1];
                this.q[i][1]= p[i][0];
            }
        }
    }
    public void setP(int [][]p){
        this.q = new float[p.length][2];
        for(int i = 0;i < p.length;i++ ){
            this.q[i][0]= p[i][1];
            this.q[i][1]= p[i][0];
        }

    }
    public void setQ(int [][]q){
        this.q = new float[p.length][2];
        for(int i = 0;i < p.length;i++ ){
            this.p[i][0]= q[i][1];
            this.p[i][1]= q[i][0];
        }

    }

    //输入y,x 输出
    public int[] afflinePoint(int[] v){
        int ctrl = this.p.length;
        //前处理，应当放置于矩阵之外

        float [][] temp = new float[ctrl][2];
        for (int i= 0; i<ctrl;i++){
            for (int j = 0; j<2;j++){
                temp[i][j] = this.p[i][j] - v[j];
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

        //阶段前者为y,后者为x
        if(transfer[0]<0 )
            transfer[0] =0;
        if(transfer[0]>height-1)
            transfer[0] = height-1;
        if(transfer[1]<0 )
            transfer[1] =0;
        if(transfer[1]>width-1)
            transfer[1] =width-1;

        return transfer;

    }

    public void changeImage() throws InterruptedException {
        for(int i=0; i<this.height;i++){
            for(int j=0; j<this.width;j++){
                int[] point = this.afflinePoint(new int[]{i,j});
                operator.setPixelColor(j,i,point[1],point[0]);
            }
        }
    }

    public Object getDeformImg(){
        this.operator.saveChange();
        return this.operator.getDeformImg();
    }
}

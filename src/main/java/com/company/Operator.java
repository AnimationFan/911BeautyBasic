package com.company;

import org.opencv.core.Mat;

public class Operator implements ImageOperator {
    Mat origin;
    Mat deform;
    int width;
    int height;
    byte[] origin_byte;
    byte[] deform_byte;
    public Operator(Mat origin){
        this.origin = origin;
        this.height = origin.height();
        this.width = origin.width();
        this.origin_byte = new byte[this.height*this.width*3];
        this.deform_byte = new byte[this.height*this.width*3];
        this.deform = new Mat(this.height,this.width,this.origin.type());
        this.origin.get(0,0,this.origin_byte);
        this.origin.get(0,0,this.deform_byte);

    }

    @Override
    public void setPixelColor(int x, int y, int sourceX, int sourceY) {
        this.deform_byte[y*width*3+x*3+0] = this.origin_byte[sourceY*width*3+sourceX*3+0];
        this.deform_byte[y*width*3+x*3+1] = this.origin_byte[sourceY*width*3+sourceX*3+1];
        this.deform_byte[y*width*3+x*3+2] = this.origin_byte[sourceY*width*3+sourceX*3+2];
    }

    @Override
    public Object getDeformImg() {

        return this.deform;
    }

    @Override
    public void saveChange() {
        this.deform.put(0,0,this.deform_byte);
    }




}

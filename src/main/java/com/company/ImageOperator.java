package com.company;

public interface ImageOperator {
    public void setPixelColor(int x,int y,int sourceX,int sourceY);
    public Object getDeformImg();
    public void saveChange();
}

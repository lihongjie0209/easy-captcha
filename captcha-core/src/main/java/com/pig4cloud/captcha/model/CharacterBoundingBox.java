package com.pig4cloud.captcha.model;

/**
 * Character bounding box information for machine learning training
 * Created for ML labeling and training purposes
 */
public class CharacterBoundingBox {
    
    private char character;
    private int x;
    private int y;
    private int width;
    private int height;
    
    public CharacterBoundingBox(char character, int x, int y, int width, int height) {
        this.character = character;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    
    public char getCharacter() {
        return character;
    }
    
    public void setCharacter(char character) {
        this.character = character;
    }
    
    public int getX() {
        return x;
    }
    
    public void setX(int x) {
        this.x = x;
    }
    
    public int getY() {
        return y;
    }
    
    public void setY(int y) {
        this.y = y;
    }
    
    public int getWidth() {
        return width;
    }
    
    public void setWidth(int width) {
        this.width = width;
    }
    
    public int getHeight() {
        return height;
    }
    
    public void setHeight(int height) {
        this.height = height;
    }
    
    @Override
    public String toString() {
        return "CharacterBoundingBox{" +
                "character=" + character +
                ", x=" + x +
                ", y=" + y +
                ", width=" + width +
                ", height=" + height +
                '}';
    }
}
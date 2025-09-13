package com.pig4cloud.captcha.example;

import com.pig4cloud.captcha.SpecCaptcha;
import com.pig4cloud.captcha.model.CharacterBoundingBox;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * 机器学习标记示例
 * 演示如何使用字符边界框功能进行机器学习训练数据标记
 */
public class MLLabelingExample {
    
    public static void main(String[] args) throws IOException {
        // 创建验证码实例
        SpecCaptcha captcha = new SpecCaptcha(200, 60, 5);
        
        // 方法1：先生成验证码，再获取边界框信息
        System.out.println("=== 方法1：分步获取 ===");
        captcha.out(new FileOutputStream("captcha_example.png"));
        List<CharacterBoundingBox> boundingBoxes1 = captcha.getCharacterBoundingBoxes();
        
        System.out.println("验证码文本: " + captcha.text());
        System.out.println("字符边界框信息:");
        for (CharacterBoundingBox bbox : boundingBoxes1) {
            System.out.println(String.format("字符 '%c': x=%d, y=%d, width=%d, height=%d", 
                bbox.getCharacter(), bbox.getX(), bbox.getY(), bbox.getWidth(), bbox.getHeight()));
        }
        
        System.out.println();
        
        // 方法2：一次性生成验证码并获取边界框信息（推荐用于机器学习）
        System.out.println("=== 方法2：一步到位（推荐） ===");
        SpecCaptcha captcha2 = new SpecCaptcha(200, 60, 5);
        List<CharacterBoundingBox> boundingBoxes2 = captcha2.outWithBoundingBoxes(
            new FileOutputStream("captcha_with_bbox.png")
        );
        
        System.out.println("验证码文本: " + captcha2.text());
        System.out.println("字符边界框信息:");
        for (CharacterBoundingBox bbox : boundingBoxes2) {
            System.out.println(String.format("字符 '%c': x=%d, y=%d, width=%d, height=%d", 
                bbox.getCharacter(), bbox.getX(), bbox.getY(), bbox.getWidth(), bbox.getHeight()));
        }
        
        // 生成用于机器学习的标注数据格式示例
        System.out.println();
        System.out.println("=== 机器学习标注格式示例 ===");
        generateMLAnnotation(captcha2.text(), boundingBoxes2, "captcha_with_bbox.png");
    }
    
    /**
     * 生成机器学习标注数据格式示例（类似YOLO格式）
     */
    private static void generateMLAnnotation(String text, List<CharacterBoundingBox> boundingBoxes, String imagePath) {
        System.out.println("图片路径: " + imagePath);
        System.out.println("验证码文本: " + text);
        System.out.println("标注信息（YOLO格式示例）:");
        
        // 假设图片尺寸为200x60
        int imageWidth = 200;
        int imageHeight = 60;
        
        for (int i = 0; i < boundingBoxes.size(); i++) {
            CharacterBoundingBox bbox = boundingBoxes.get(i);
            
            // 计算归一化坐标
            double centerX = (bbox.getX() + bbox.getWidth() / 2.0) / imageWidth;
            double centerY = (bbox.getY() + bbox.getHeight() / 2.0) / imageHeight;
            double width = bbox.getWidth() / (double) imageWidth;
            double height = bbox.getHeight() / (double) imageHeight;
            
            // 输出格式：class_id center_x center_y width height
            System.out.println(String.format("%d %.6f %.6f %.6f %.6f  # 字符: %c", 
                i, centerX, centerY, width, height, bbox.getCharacter()));
        }
    }
}
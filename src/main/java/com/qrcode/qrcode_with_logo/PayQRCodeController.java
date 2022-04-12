package com.qrcode.qrcode_with_logo;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.FileSystems;
import java.nio.file.Path;

@Controller
public class PayQRCodeController {

    @Autowired
    private ResourceLoader resourceLoader;

    @PostMapping("/createAccount")
    public String createNewAccount(@ModelAttribute("request") CreateAccountRequest request, Model model)
            throws WriterException, IOException {
        String qrCodePath = writeQR(request);
        model.addAttribute("code", qrCodePath);
        return "QRcode";
    }

    @GetMapping("/readQR")
    public String verifyQR(@RequestParam("qrImage") String qrImage, Model model) throws Exception {
        model.addAttribute("content", readQR(qrImage));
        model.addAttribute("code", qrImage);
        return "QRcode";

    }

    private String writeQR(CreateAccountRequest request) throws WriterException, IOException {
        String qcodePath = "src/main/resources/static/" + request.getName() + "-QRCode.png";
        String logo = "src/main/resources/static/" + request.getName() + "logo.png";
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(request.getName() + "\n" + request.getEmail() + "\n"
                + request.getMobile() + "\n" + request.getPassword(), BarcodeFormat.QR_CODE, 350, 350);
        Path path = FileSystems.getDefault().getPath(qcodePath);
        //Path path1= FileSystems.getDefault().getPath(logo);
       /* MatrixToImageConfig imageConfig = new MatrixToImageConfig(MatrixToImageConfig.BLACK, MatrixToImageConfig.WHITE);
        BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(bitMatrix, imageConfig);
        BufferedImage logoImage = ImageIO.read( new File(String.valueOf(path1)));
        int finalImageHeight = qrImage.getHeight() - logoImage.getHeight();
        int finalImageWidth = qrImage.getWidth() - logoImage.getWidth();
        //Merging both images
        BufferedImage finalImage = new BufferedImage(qrImage.getHeight(), qrImage.getWidth(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = (Graphics2D) finalImage.getGraphics();
        graphics.drawImage(qrImage, 0, 0, null);
        graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        graphics.drawImage(logoImage, (int) Math.round(finalImageWidth / 2), (int) Math.round(finalImageHeight / 2), null);
       MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);
        return "/static/" + request.getName() + "-QRCode.png";
    }*/

 return null;
    }
    private String readQR(String qrImage) throws Exception {
        final Resource fileResource = resourceLoader.getResource("classpath:static/" + qrImage);
        File QRfile = fileResource.getFile();
        BufferedImage bufferedImg = ImageIO.read(QRfile);
        LuminanceSource source = new BufferedImageLuminanceSource(bufferedImg);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
        Result result = new MultiFormatReader().decode(bitmap);
        System.out.println("Barcode Format: " + result.getBarcodeFormat());
        System.out.println("Content: " + result.getText());
        return result.getText();

    }
}
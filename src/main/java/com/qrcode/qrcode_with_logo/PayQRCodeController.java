package com.qrcode.qrcode_with_logo;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
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
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

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
        Map hints = new HashMap();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);

        String qcodePath = "src/main/resources/static/images/" + request.getName() + "-QRCode.png";
        String logo = "src/main/resources/static/images/" + request.getName() + "logo.png";
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BitMatrix bitMatrix = qrCodeWriter.encode(request.getName() + "\n" + request.getEmail() + "\n"
                + request.getMobile() + "\n" + request.getPassword(), BarcodeFormat.QR_CODE, 350, 350, hints);
        MatrixToImageConfig config = new MatrixToImageConfig(MatrixToImageConfig.BLACK, MatrixToImageConfig.WHITE);
        BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(bitMatrix, config);
        BufferedImage logoImage = ImageIO.read(new File(logo));
        int deltaHeight = qrImage.getHeight() - logoImage.getHeight();
        int deltaWidth = qrImage.getWidth() - logoImage.getWidth();
        //combined
        BufferedImage combined = new BufferedImage(qrImage.getHeight(), qrImage.getWidth(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D) combined.getGraphics();

        g.drawImage(qrImage, 0, 0, null);
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        g.drawImage(logoImage, (int) Math.round(deltaWidth / 2), (int) Math.round(deltaHeight / 2), null);
        Path path = FileSystems.getDefault().getPath(String.valueOf(combined));
        MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);
        return "/static/images/" + request.getName() + "-combined.png";
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
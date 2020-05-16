package converter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

/**
 * @author wangyong
 * @since 2020/5/16
 */
public class PdfConverter {

    public static void convertPdfToDocTheSamePath(String srcPath, String pdfFileName) {
        convertPdfToDoc(srcPath, pdfFileName, srcPath);
    }

    public static void convertPdfToDoc(String srcPath, String pdfFileName, String targetPath) {
        if (StringUtils.isBlank(srcPath) || StringUtils.isBlank(pdfFileName) || StringUtils.isBlank(targetPath)) {
            throw new IllegalArgumentException("input param is blank, please check the params");
        }

        String suffix = pdfFileName.substring(pdfFileName.lastIndexOf(".") + 1);
        if (!Objects.equals(suffix, "pdf")) {
            throw new IllegalArgumentException("the file wait to convert is not a pdf file.");
        }

        try (PDDocument srgDocument = PDDocument.load(new File(srcPath + "//" + pdfFileName))) {
            String targetFilePath = targetPath + "//" + pdfFileName.substring(0, pdfFileName.lastIndexOf(".")) + ".doc";
            File targetFile = new File(targetFilePath);
            if (!targetFile.exists()) {
                targetFile.createNewFile();
            }

            Writer writer = new OutputStreamWriter(new FileOutputStream(targetFile), "UTF-8");
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);
            stripper.setStartPage(1);
            stripper.setEndPage(srgDocument.getNumberOfPages());
            stripper.writeText(srgDocument, writer);
            writer.close();
            srgDocument.close();
            System.out.println(srcPath + pdfFileName + " convert success, targetFile is " + targetFilePath);
        }
        catch (IOException e) {
            System.out.println(srcPath + pdfFileName + " convert fail.");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        convertPdfToDocTheSamePath("D:\\勇勇资料\\电子书", "深入理解Java虚拟机：JVM高级特性与最佳实践（第3版）(华章原创精品)-周志明.pdf");
    }
}

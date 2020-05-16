package utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.multipdf.Splitter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.BreakType;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
import com.itextpdf.text.pdf.parser.SimpleTextExtractionStrategy;
import com.itextpdf.text.pdf.parser.TextExtractionStrategy;

/**
 * @author wangyong
 * @since 2020/5/16
 */
public class PdfUtils {

    public static void convertToWordOfText(String srcPath, String pdfFileName) {
        convertToWordOfText(srcPath, pdfFileName, srcPath);
    }

    public static void convertToWordOfText(String srcPath, String fileName, String targetPath) {
        if (StringUtils.isBlank(srcPath) || StringUtils.isBlank(fileName) || StringUtils.isBlank(targetPath)) {
            throw new IllegalArgumentException("input param is blank, please check the params");
        }

        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
        if (!Objects.equals(suffix, "pdf")) {
            throw new IllegalArgumentException("the file wait to convert is not a pdf file.");
        }

        try (PDDocument srgDocument = PDDocument.load(new File(srcPath + "//" + fileName))) {
            String targetFilePath = targetPath + "//" + fileName.substring(0, fileName.lastIndexOf(".")) + ".doc";
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
            System.out.println(srcPath + fileName + " convert success, targetFile is " + targetFilePath);
        }
        catch (IOException e) {
            System.out.println(srcPath + fileName + " convert fail.");
            e.printStackTrace();
        }
    }


    public static void convertToWord(String srcPath, String fileName, String targetPath) {
        if (StringUtils.isBlank(srcPath) || StringUtils.isBlank(fileName) || StringUtils.isBlank(targetPath)) {
            throw new IllegalArgumentException("input param is blank, please check the params");
        }

        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
        if (!Objects.equals(suffix, "pdf")) {
            throw new IllegalArgumentException("the file wait to convert is not a pdf file.");
        }

        try {
            PdfReader reader = new PdfReader(srcPath + "//" + fileName);
            PdfReaderContentParser parser = new PdfReaderContentParser(reader);

            XWPFDocument doc = new XWPFDocument();
            for (int i = 1; i <= reader.getNumberOfPages(); i++) {
                TextExtractionStrategy strategy =
                        parser.processContent(i, new SimpleTextExtractionStrategy());
                String text = strategy.getResultantText();
                XWPFParagraph p = doc.createParagraph();
                XWPFRun run = p.createRun();
                run.setText(text);
                run.addBreak(BreakType.PAGE);
            }

            String targetFilePath = targetPath + "//" + fileName.substring(0, fileName.lastIndexOf(".")) + ".doc";
            FileOutputStream out = new FileOutputStream(targetFilePath);
            doc.write(out);
            System.out.println(srcPath + fileName + " convert success, targetFile is " + targetFilePath);
        }
        catch (IOException e) {
            System.out.println(srcPath + fileName + " convert fail.");
            e.printStackTrace();
        }
    }

    public static void split(String srcPath, String fileName, int startPage, int splitPageSize, String targetPath) {
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
        if (!Objects.equals(suffix, "pdf")) {
            throw new IllegalArgumentException("the file wait to convert is not a pdf file.");
        }

        File resource = new File(srcPath + "//" + fileName);
        try (PDDocument doc = PDDocument.load(resource)) {
            Splitter split = new Splitter();
            split.setStartPage(startPage);
            split.setSplitAtPage(splitPageSize);
            List<PDDocument> docs = split.split(doc);
            int i = 1;
            for (PDDocument document : docs) {
                String targetFilePath = targetPath + "//" + fileName.substring(0, fileName.lastIndexOf(".")) + i + ".pdf";
                File file = new File(targetFilePath);
                if (!file.exists()) {
                    file.createNewFile();
                }
                FileOutputStream fos = new FileOutputStream(file);
                document.save(fos);
                document.close();
                i++;
            }
            doc.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        convertToWordOfText("D:\\勇勇资料\\电子书", "深入理解Java虚拟机：JVM高级特性与最佳实践（第3版）(华章原创精品)-周志明.pdf");
    }
}

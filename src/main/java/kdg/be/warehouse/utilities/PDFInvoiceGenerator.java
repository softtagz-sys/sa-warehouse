package kdg.be.warehouse.utilities;

import kdg.be.warehouse.domain.invoicing.Invoice;
import kdg.be.warehouse.domain.invoicing.InvoiceLine;
import kdg.be.warehouse.service.StorageCostService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Component
public class PDFInvoiceGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(PDFInvoiceGenerator.class);

    public void generateInvoice(Invoice customerInvoice) throws RuntimeException {
        String html = parseInvoiceTemplate(customerInvoice);
        generatePDFFromHTML(html, customerInvoice.getCustomer().getName(), LocalDate.now())
                .orElseThrow( () -> new RuntimeException("Something went wrong with the generation of PDFs"));
}

    private String parseInvoiceTemplate(Invoice invoice) {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);

        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);

        Context context = new Context();
        context.setVariable("invoice", invoice);
        context.setVariable("totalAmount", invoice.getInvoiceLines().stream().map(InvoiceLine::getTotalPrice).reduce(0f, Float::sum));

        return templateEngine.process("invoice-template", context);
    }

    private void checkAndCreateFolder(String folderPath){
        File folder = new File(folderPath);
        if(!folder.exists()){
            if(folder.mkdirs()) {
                LOGGER.info("Folder {} created", folderPath);
            } else {
                LOGGER.error("Folder {} could not be created", folderPath);
                throw new RuntimeException("Folder " + folderPath + " could not be created");
            }
        }
    }

    private Optional<String> generatePDFFromHTML(String html, String customer, LocalDate date) {
        String folderPath = System.getProperty("user.home") + File.separator + "Downloads" + File.separator + "SA_Invoices" + File.separator + customer;

        try {
            checkAndCreateFolder(folderPath);
        } catch (RuntimeException e) {
            return Optional.empty();
        }

        String filePath = folderPath + File.separator + date.toString() + ".pdf";

         try (OutputStream outputStream = new FileOutputStream(filePath)) {
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(html);
            renderer.layout();
            renderer.createPDF(outputStream);
            return Optional.of(filePath);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return Optional.empty();
        }


    }
}

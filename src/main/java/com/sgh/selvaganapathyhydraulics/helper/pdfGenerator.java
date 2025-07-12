package com.sgh.selvaganapathyhydraulics.helper;

import java.io.ByteArrayOutputStream;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;

@Component
public class pdfGenerator {

    public static ByteArrayOutputStream generate(String templateName, Map<String, Object> model) {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("templates/"); // ✅ Correct prefix if your invoice.html is in src/main/resources/templates/
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode("HTML");
        templateResolver.setCharacterEncoding("UTF-8");
        templateResolver.setCacheable(false); // optional during dev

        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);

        Context context = new Context();
        model.forEach(context::setVariable);

        String htmlContent = templateEngine.process(templateName, context);

        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(htmlContent);
            // It's good practice to set a base URL for relative paths if you have any,
            // though for Base64 embedded images, it's less critical.
            // renderer.setSharedContext(renderer.getSharedContext());
            // renderer.getSharedContext().setBaseURL("file:/your/base/path/"); // If you had external images

            renderer.layout();
            renderer.createPDF(outputStream);
            return outputStream;
        } catch (Exception e) {
            throw new RuntimeException("PDF generation failed", e);
        }
    }
}
package com.sgh.selvaganapathyhydraulics.helper;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.util.Map;

public class pdfGenerator {

    public static ByteArrayOutputStream generate(String templateName, Map<String, Object> model) {
    	ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
    	//templateResolver.setPrefix("templates/"); // ✅ No leading slash
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
            renderer.layout();
            renderer.createPDF(outputStream);
            return outputStream;
        } catch (Exception e) {
            throw new RuntimeException("PDF generation failed", e);
        }
    }
}

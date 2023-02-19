package com.shoppingcart.admin.brand.export;

import com.shoppingcart.admin.AbstractExporter;
import com.shoppingcart.admin.entity.Brand;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class BrandCsvExporter extends AbstractExporter {
    public void export(List<Brand> listBrands, HttpServletResponse response) throws IOException{
        super.setResponseHeader(response, "text/csv", ".csv", "Brand_");
        ICsvBeanWriter csvWriter = new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE);

        String[] csvHeader = {"Brand ID", "Name", "Logo"};
        String[] fieldMapping ={"id","name","logo"};

        csvWriter.writeHeader(csvHeader);

        for (Brand brand: listBrands){
            csvWriter.write(brand, fieldMapping);
        }csvWriter.close();

    }

}

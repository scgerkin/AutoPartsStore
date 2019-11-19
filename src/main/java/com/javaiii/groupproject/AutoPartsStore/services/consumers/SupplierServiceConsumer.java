package com.javaiii.groupproject.AutoPartsStore.services.consumers;

import com.javaiii.groupproject.AutoPartsStore.Models.business.Supplier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SupplierServiceConsumer {

    @RequestMapping(value = "/supplier-portal")
    public String initSupplierPortalDisplay(Model model) {
        return "services/supplierServicePortal";
    }
}

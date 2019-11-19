package com.javaiii.groupproject.AutoPartsStore.services.consumers;

import com.javaiii.groupproject.AutoPartsStore.Models.address.Address;
import com.javaiii.groupproject.AutoPartsStore.Models.business.Supplier;
import com.javaiii.groupproject.AutoPartsStore.services.consumers.commands.*;
import com.sun.jndi.toolkit.url.Uri;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriBuilder;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;

@Controller
public class SupplierServiceConsumer {

    private final String endpoint = "http://localhost:8080/add-new-supplier/";

    @RequestMapping(value = "services/supplierPortal")
    public String initSupplierPortalDisplay(Model model) {
        return "services/supplierServicePortal";
    }

    @GetMapping(value = "services/newSupplier")
    public String initSupplierAddPortalDisplay(Model model) {
        model.addAttribute("supplier", new SupplierCommand());
        return "services/newSupplier";
    }

    @PostMapping(value = "services/newSupplier")
    public String postNewSupplier(@ModelAttribute("supplier") SupplierCommand cmd,
                                  BindingResult bindingResult,
                                  Model model,
                                  RedirectAttributes redirectAttributes) {
        System.out.println("POST SUBMIT");
        String resource = null;
        try {
            resource = unpackCommand(cmd);
            System.out.println(resource);
        }
        catch (UnsupportedEncodingException ex) {
            System.out.println("ENCODING PROBLEM");
        }
        String uri = endpoint + resource;
        RestTemplate restTemplate = new RestTemplate();
        Integer supplierId = restTemplate.getForObject(uri, Integer.class);
        System.out.println("POST SUCCESS Supplier ID: " + supplierId);
        return "services/supplierServicePortal";
    }

    private String unpackCommand(SupplierCommand cmd) throws UnsupportedEncodingException {
        String result = URLEncoder.encode(cmd.getCompanyName(),"UTF-8") + "/" +
                            URLEncoder.encode(cmd.getContactPerson(),"UTF-8") + "/" +
                            URLEncoder.encode(cmd.getPrimaryPhone(),"UTF-8") + "/" +
                            URLEncoder.encode(cmd.getSecondaryPhone(),"UTF-8") + "/" +
                            URLEncoder.encode(cmd.getWebsite(),"UTF-8") + "/" +
                            URLEncoder.encode(cmd.getStreet(),"UTF-8") + "/" +
                            URLEncoder.encode(cmd.getCity(),"UTF-8") + "/" +
                            URLEncoder.encode(cmd.getState(),"UTF-8") + "/" +
                            URLEncoder.encode(cmd.getZipCode(),"UTF-8") + "/" +
                            URLEncoder.encode(cmd.getNotes(),"UTF-8");
        return result;
    }



}

package com.javaiii.groupproject.AutoPartsStore.services.consumers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.javaiii.groupproject.AutoPartsStore.Models.address.Address;
import com.javaiii.groupproject.AutoPartsStore.Models.business.Supplier;
import com.javaiii.groupproject.AutoPartsStore.command.IdCommand;
import com.javaiii.groupproject.AutoPartsStore.services.consumers.commands.*;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        Supplier supplier = unpackCommand(cmd);
        HttpEntity<String> request = new HttpEntity<>(convertToJson(supplier), httpHeaders);
        Integer supplierId = restTemplate.postForObject(endpoint, request, Integer.class);
        redirectAttributes.addFlashAttribute("id", new IdCommand(supplierId));
        return "redirect:/services/addSupplierSuccess";
    }

    @RequestMapping(value="/services/addSupplierSuccess")
    public String initSuccessDisplay(@ModelAttribute("id") IdCommand id,
                                     BindingResult bindingResult,
                                     Model model,
                                     RedirectAttributes redirectAttributes) {
        model.addAttribute("id", id);
        return "services/addSupplierSuccess";
    }


    private Supplier unpackCommand(SupplierCommand command) {
        Address address = new Address(command.getStreet(), command.getCity(),
            command.getState(), command.getZipCode());
        Supplier supplier = Supplier.createNew(command.getCompanyName(),
            command.getContactPerson(), command.getPrimaryPhone(), command.getSecondaryPhone(),
            command.getWebsite(), address, command.getNotes());
        return supplier;
    }

    private String convertToJson(Supplier supplier) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(supplier);
        }
        catch (JsonProcessingException ex) {
            System.out.println("COULD NOT PARSE TO JSON");
        }
        return "";
    }

}

package com.javaiii.groupproject.AutoPartsStore.services.consumers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.javaiii.groupproject.AutoPartsStore.Models.address.Address;
import com.javaiii.groupproject.AutoPartsStore.Models.business.Supplier;
import com.javaiii.groupproject.AutoPartsStore.command.IdCommand;
import com.javaiii.groupproject.AutoPartsStore.services.consumers.commands.*;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class SupplierServiceConsumer {

    private final String host = "https://jgp-aps.herokuapp.com/";

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
        final String endpoint = host + "/add-new-supplier/";
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

    @GetMapping(value = "/services/checkQuantity")
    public String initCheckQuantityDisplay(Model model) {
        PartQuantityCommand partQuantityCommand = new PartQuantityCommand();
        partQuantityCommand.setQuantity(-2);
        partQuantityCommand.setStatus("Awaiting input...");
        model.addAttribute("partCommand", partQuantityCommand);
        return "services/checkPartQuantity";
    }

    @PostMapping(value = "/services/checkQuantity")
    public String postCheckQuantity(@ModelAttribute("partCommand") PartQuantityCommand partCommand,
                                    BindingResult bindingResult,
                                    Model model,
                                    RedirectAttributes redirectAttributes) {
        final String endpoint = host + "/quantity-on-hand/";
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(convertToJson(partCommand), httpHeaders);
        try {
            ResponseEntity<Integer> result = restTemplate.exchange(endpoint, HttpMethod.POST, request, Integer.class);
            partCommand.setQuantity(result.getBody());
            partCommand.setStatus("Received");
        }
        catch (HttpClientErrorException ex) {
            partCommand.setQuantity(-1);
            partCommand.setStatus("Bad request");
        }
        redirectAttributes.addFlashAttribute("partCommand", partCommand);
        return "services/checkPartQuantity";
    }

    @GetMapping(value = "/services/updateQuantity")
    public String initUpdateQuantityDisplay(Model model) {
        PartQuantityCommand partQuantityCommand = new PartQuantityCommand();
        partQuantityCommand.setStatus("Awaiting input...");
        model.addAttribute("partCommand", partQuantityCommand);
        return "services/updatePartQuantity";
    }

    @PostMapping(value = "/services/updateQuantity")
    public String postUpdateQuantity(@ModelAttribute("partCommand") PartQuantityCommand partCommand,
                                     BindingResult bindingResult,
                                     Model model,
                                     RedirectAttributes redirectAttributes) {
        final String endpoint = host + "/update-quantity/";
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(convertToJson(partCommand), httpHeaders);
        try {
            ResponseEntity<Integer> result = restTemplate.exchange(endpoint, HttpMethod.POST, request, Integer.class);
            partCommand.setStatus("Part updated successfully!");
        }
        catch (HttpClientErrorException ex) {
            partCommand.setStatus("Invalid request");
        }
        redirectAttributes.addFlashAttribute("partCommand", partCommand);
        return "services/updatePartQuantity";
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

    private String convertToJson(PartQuantityCommand cmd) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(cmd);
        }
        catch (JsonProcessingException ex) {
            System.out.println("COULD NOT PARSE TO JSON");
        }
        return "";
    }

}

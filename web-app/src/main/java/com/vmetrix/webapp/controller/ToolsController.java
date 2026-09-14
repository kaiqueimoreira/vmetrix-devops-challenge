package com.vmetrix.webapp.controller;

import com.vmetrix.calc.FinancialCalc;
import com.vmetrix.calc.StatisticsCalc;
import com.vmetrix.misc.StringUtils;
import com.vmetrix.misc.ValidationUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class ToolsController {

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("page", "home");
        return "index";
    }

    // ── String Tools ───────────────────────────────────────────────────

    @GetMapping("/tools/string")
    public String stringToolsForm(Model model) {
        model.addAttribute("page", "string");
        return "string-tools";
    }

    @PostMapping("/tools/string/reverse")
    public String reverseString(@RequestParam String input, Model model) {
        model.addAttribute("page", "string");
        model.addAttribute("operation", "Reverse");
        model.addAttribute("input", input);
        model.addAttribute("result", StringUtils.reverse(input));
        return "string-tools";
    }

    @PostMapping("/tools/string/palindrome")
    public String checkPalindrome(@RequestParam String input, Model model) {
        model.addAttribute("page", "string");
        model.addAttribute("operation", "Palindrome Check");
        model.addAttribute("input", input);
        model.addAttribute("result", StringUtils.isPalindrome(input) ? "✅ Yes, it is a palindrome" : "❌ No, it is not a palindrome");
        return "string-tools";
    }

    @PostMapping("/tools/string/title-case")
    public String titleCase(@RequestParam String input, Model model) {
        model.addAttribute("page", "string");
        model.addAttribute("operation", "Title Case");
        model.addAttribute("input", input);
        model.addAttribute("result", StringUtils.toTitleCase(input));
        return "string-tools";
    }

    // ── Validation Tools ───────────────────────────────────────────────

    @GetMapping("/tools/validation")
    public String validationForm(Model model) {
        model.addAttribute("page", "validation");
        return "validation-tools";
    }

    @PostMapping("/tools/validation/email")
    public String validateEmail(@RequestParam String email, Model model) {
        model.addAttribute("page", "validation");
        model.addAttribute("operation", "Email Validation");
        model.addAttribute("input", email);
        model.addAttribute("result", ValidationUtils.isValidEmail(email)
                ? "✅ Valid email address" : "❌ Invalid email address");
        return "validation-tools";
    }

    @PostMapping("/tools/validation/cpf")
    public String validateCpf(@RequestParam String cpf, Model model) {
        model.addAttribute("page", "validation");
        model.addAttribute("operation", "CPF Validation");
        model.addAttribute("input", cpf);
        model.addAttribute("result", ValidationUtils.isValidCpf(cpf)
                ? "✅ Valid CPF" : "❌ Invalid CPF");
        return "validation-tools";
    }

    // ── Financial Tools ────────────────────────────────────────────────

    @GetMapping("/tools/financial")
    public String financialForm(Model model) {
        model.addAttribute("page", "financial");
        return "financial-tools";
    }

    @PostMapping("/tools/financial/compound")
    public String compoundInterest(@RequestParam double principal,
                                   @RequestParam double rate,
                                   @RequestParam int periods,
                                   @RequestParam double years,
                                   Model model) {
        model.addAttribute("page", "financial");
        model.addAttribute("operation", "Compound Interest");
        var result = FinancialCalc.compoundInterest(principal, rate, periods, years);
        model.addAttribute("result", "Final amount: " + result);
        return "financial-tools";
    }

    @PostMapping("/tools/financial/discount")
    public String applyDiscount(@RequestParam double value,
                                @RequestParam double percentage,
                                Model model) {
        model.addAttribute("page", "financial");
        model.addAttribute("operation", "Discount");
        var result = FinancialCalc.applyDiscount(value, percentage);
        model.addAttribute("result", "Discounted value: " + result);
        return "financial-tools";
    }

    // ── Statistics Tools ───────────────────────────────────────────────

    @GetMapping("/tools/statistics")
    public String statsForm(Model model) {
        model.addAttribute("page", "statistics");
        return "statistics-tools";
    }

    @PostMapping("/tools/statistics/summary")
    public String statsSummary(@RequestParam String values, Model model) {
        model.addAttribute("page", "statistics");
        model.addAttribute("operation", "Statistical Summary");
        List<Double> list = Arrays.stream(values.split("[,\\s]+"))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .map(Double::parseDouble)
                .collect(Collectors.toList());
        model.addAttribute("mean",   StatisticsCalc.mean(list));
        model.addAttribute("median", StatisticsCalc.median(list));
        model.addAttribute("stdDev", StatisticsCalc.standardDeviation(list));
        model.addAttribute("min",    StatisticsCalc.min(list));
        model.addAttribute("max",    StatisticsCalc.max(list));
        model.addAttribute("sum",    StatisticsCalc.sum(list));
        return "statistics-tools";
    }
}

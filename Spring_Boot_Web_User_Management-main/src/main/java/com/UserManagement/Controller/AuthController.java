package com.UserManagement.Controller;

import com.UserManagement.Dto.UserDto;
import com.UserManagement.Entity.User;
import com.UserManagement.service.UserService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
public class AuthController {

	@Autowired
	private UserService userService;

	public AuthController(UserService userService) {
		this.userService = userService;
	}

	@GetMapping("/")
	public String home() {
		return "redirect:/login";
	}

	@GetMapping("/login")
	public String loginForm() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (isAuthenticated(authentication)) {
			return "redirect:/users";
		}
		return "login";
	}

	@GetMapping("/register")
	public String showRegistrationForm(Model model) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (isAuthenticated(authentication)) {
			return "redirect:/users";
		}
		model.addAttribute("user", new UserDto());
		return "register";
	}

	@PostMapping("/register/save")
	public String registration(@Valid @ModelAttribute("user") UserDto userDto, BindingResult result, Model model) {
		validateUser(userDto, result);
		if (result.hasErrors()) {
			model.addAttribute("user", userDto);
			return "register";
		}
		userService.saveUser(userDto);
		return "redirect:/register?success=true";
	}

	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/add")
	public String showUserAddForm(Model model) {
		model.addAttribute("user", new UserDto());
		return "add";
	}

	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping("/add/save")
	public String addUser(@Valid @ModelAttribute("user") UserDto userDto, BindingResult result, Model model) {
		validateUser(userDto, result);
		if (result.hasErrors()) {
			model.addAttribute("user", userDto);
			return "add";
		}
		userService.saveUser(userDto);
		return "redirect:/users?success=true";
	}

	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/users")
	public String users(Model model) {
		List<UserDto> users = userService.findAllUsers();
		model.addAttribute("users", users);
		return "user";
	}

	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/edit/{id}")
	public String editUser(@PathVariable Long id, Model model) {
		UserDto user = userService.findUserById(id);
		model.addAttribute("user", user);
		return "edit";
	}

	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping("/edit/{id}")
	public String updateUserById(@Valid @ModelAttribute("user") UserDto updatedUserDto, BindingResult result, @PathVariable Long id, Model model) {
		if (updatedUserDto.getPassword() != null && updatedUserDto.getPassword().length() < 7) {
			result.rejectValue("password", "field.min.length", "Password should have at least 7 characters");
		}
		if (result.hasErrors()) {
			model.addAttribute("user", updatedUserDto);
			return "edit";
		}
		userService.editUser(updatedUserDto, id);
		return "redirect:/users";
	}

	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/delete/{id}")
	public String deleteUser(@PathVariable Long id, Principal principal, RedirectAttributes redirectAttributes) {
		String loggedInUsername = principal.getName();
		User loggedInUser = userService.findUserByEmail(loggedInUsername);

		if (loggedInUser != null && loggedInUser.getId().equals(id)) {
			redirectAttributes.addFlashAttribute("error", "You cannot delete yourself.");
		} else if (userService.doesUserExist(id)) {
			userService.deleteUserById(id);
			redirectAttributes.addFlashAttribute("success", "User has been deleted successfully");
		} else {
			redirectAttributes.addFlashAttribute("error", "User does not exist");
		}
		return "redirect:/users";
	}

	private boolean isAuthenticated(Authentication authentication) {
		return authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal());
	}

	private void validateUser(UserDto userDto, BindingResult result) {
		User existingUser = userService.findUserByEmail(userDto.getEmail());
		if (existingUser != null && existingUser.getEmail() != null && !existingUser.getEmail().isEmpty()) {
			result.rejectValue("email", null, "There is already an account registered with the same email");
		}
		if (userDto.getPassword() != null && userDto.getPassword().length() < 7) {
			result.rejectValue("password", "field.min.length", "Password should have at least 7 characters");
		} else if (userDto.getPassword() == null) {
			result.rejectValue("password", "field.required", "Password should not be empty.");
		}
	}
}

package THJava.Ngay3.Books.Controllers;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.StringUtils;

import THJava.Ngay3.Books.Models.CartItem;
import THJava.Ngay3.Books.Models.User;
import THJava.Ngay3.Books.Services.CartItemService;
import THJava.Ngay3.Books.Services.RoleService;
import THJava.Ngay3.Books.Services.UserService;
import THJava.Ngay3.Books.Utils.FileUploadUtil;

@Controller
@RequestMapping("/users")
@ComponentScan("THJava.Ngay3.Books.Services")
public class UserController {
	@Autowired
	private UserService userService;
	@Autowired
	private RoleService roleService;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private CartItemService cartItemService;
	
	@GetMapping
	public String viewAllUser(Model model,Principal principal) {
		List<User> listUsers = userService.listAll();
		model.addAttribute("users", listUsers);
		String username = principal.getName();
        User user = userService.findByUsername(username);
        List<CartItem> cartItems = cartItemService.getCartItems(user);
        model.addAttribute("count", cartItems.size());
		return "user/index";
	}
	
	@GetMapping("/new")
	public String showNewUserPage(Model model) {
		User user = new User();
		model.addAttribute("user", user);
		model.addAttribute("roles", roleService.listAll());
		return "user/create";
	}
	
	@PostMapping("/save")
	public String saveUser(@ModelAttribute("user") User user, @RequestParam("image")  MultipartFile  multipartFile) 
			throws IOException{
		if (user.getPassword().isEmpty()) {
			user.setPassword(userService.get(user.getId()).getPassword());
		}else {
			user.setPassword(passwordEncoder.encode(user.getPassword()));
		}
		String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
		user.setPhotourl(fileName);
        user.setEnabled(true);
        User saveUser = userService.save(user);
        if (!multipartFile.getOriginalFilename().isBlank())
        {
            String uploadDir = "photos/" + saveUser.getId();
            FileUploadUtil.saveFile(uploadDir,fileName,multipartFile);
        }
        userService.save(user);
		
		return "redirect:/users";
	}
	@GetMapping("/edit/{id}")
	public String showEditUserPage(@PathVariable("id") Long id, Model model) {
		User user = userService.get(id);
		
		if(user == null) {
			return "notfound";
		} else {
			model.addAttribute("user", user);
			model.addAttribute("roles", roleService.listAll());
			return "user/edit";
		}
	}
	
	@GetMapping("/delete/{id}")
	public String deleteRUser(@PathVariable("id") Long id) {
		User user = userService.get(id);
		if(user == null) {
			return "notfound";
		} else {
			userService.delete(id);
			return "redirect:/users";
		}
	}
	
}

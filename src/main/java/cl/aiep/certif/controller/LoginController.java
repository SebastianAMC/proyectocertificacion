package cl.aiep.certif.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import cl.aiep.certif.controller.service.CursoService;
import cl.aiep.certif.controller.service.EstudianteService;
import cl.aiep.certif.dao.dto.EstudianteDTO;
import cl.aiep.certif.util.CommonUtil;

@Controller
public class LoginController {
	
	@Autowired
	EstudianteService serviceEst;
	
	@Autowired
	CursoService serviceCurso;
	
	@GetMapping("/")
	public String homeCursos() {
		
		return "index";
	}

	@GetMapping("/login" )
    public String viewLoginPage() {
      
		return "login";
    }
	
	@GetMapping("/registrate")
    public String viewRegister(final Model model) {
			model.addAttribute("estudiante", new EstudianteDTO());
			model.addAttribute("regiones", serviceEst.obtienRegiones());
	     return "nuevo";
		
    }
	
	@GetMapping("/Mipanel")
	public String redireccion(final Model model) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String role = auth.getAuthorities().toString();
		if (role.contains("ADMIN")) {
			model.addAttribute("cursos", serviceCurso.obtenerCursos());
	        return "admin/indexAdmin";
		} else if (role.contains("USER")) {
			model.addAttribute("curso", serviceEst.obtenerCurso( auth.getName()));
			model.addAttribute("cursos", serviceCurso.obtenerCursos());
			String rut= auth.getName();
			model.addAttribute("estudiante", serviceEst.obtenerCurso(rut));
			return "users/panel";
		}
		return "redirect:/login";
    }
	
	
	@PostMapping("/guardar")
    public String guardar(@Valid EstudianteDTO estudiante, BindingResult result, final Model model) {
		if (result.hasErrors()) {
			model.addAttribute("estudiante", estudiante);
			model.addAttribute("regiones", serviceEst.obtienRegiones());
	        model.addAttribute("mensaje", result.getFieldError().getDefaultMessage());
	            return "nuevo";
	        }else {
	        	
	        }
		
			if(CommonUtil.validarRut(estudiante.getRut()))
					serviceEst.guardaEstudiante(estudiante);
			else {
				model.addAttribute("estudiante", estudiante);
				model.addAttribute("regiones", serviceEst.obtienRegiones());
		        model.addAttribute("mensaje", "Rut Invalido");
				return "nuevo";
				
			}
			
	        return "redirect:/login";
		
    }
	
}

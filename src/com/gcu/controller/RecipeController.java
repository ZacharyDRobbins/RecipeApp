package com.gcu.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.gcu.business.RecipeBusinessInterface;
import com.gcu.models.Principle;
import com.gcu.models.Recipe;

@Controller
@RequestMapping("/recipe")
public class RecipeController {

	//Initialize the recipeService
	RecipeBusinessInterface recipeService;

	//Initialize session
	@Autowired
	Principle principle;

	/**
	 * This method will direct the user to the recipe form.
	 * 
	 * @return ModelAndView Object that contains the view of where the user will be directed
	 */
	@RequestMapping(path = "/recipeForm", method = RequestMethod.GET)
	public ModelAndView displayRecipeForm() {
		return new ModelAndView("recipeForm", "recipe", new Recipe());
	}

	/**
	 * This method will post/add the recipe to the database by calling the business service.
	 * 
	 * @param recipe - Recipe added to the database
	 * @param result - BindingResult
	 * @return ModelAndView Class
	 */
	@RequestMapping(path = "/postRecipe", method = RequestMethod.POST)
	public ModelAndView postRecipe(@Valid @ModelAttribute("recipe") Recipe recipe, BindingResult result) {
		if (result.hasErrors()) {
			return new ModelAndView("recipeForm", "recipe", recipe);
		}

		try {
			// Access Database to post recipe
			recipeService.addRecipe(recipe, principle.getUserID());

			return this.viewUserRecipe();
		}

		catch (Exception e) {
			return new ModelAndView("errorPage");
		}
	}

	/**
	 * This method will direct the user to view the User Recipes which are unique to. 
	 * 
	 * @return ModelAndView Class
	 */
	@RequestMapping(path = "/viewUserRecipe", method = RequestMethod.GET)
	public ModelAndView viewUserRecipe() {
		ModelAndView modelAndView = new ModelAndView("usersRecipes", "recipes",
				recipeService.getUserRecipes(principle.getUserID()));
		modelAndView.addObject("userID", principle.getUserID());
		return modelAndView;
	}

	/**
	 * This method will display the content of the recipe and give the user the ability to edit or delete the recipe.
	 * 
	 * @param recipeName - Used to find the recipe
	 * @param recipeNutritionalInformation - Used to find the recipe
	 * @param recipePrice - Used to find the recipe
	 * @return ModelAndView Class
	 */
	@RequestMapping(path = "/fullRecipePost", method = RequestMethod.POST)
	public ModelAndView displayRecipePost(String recipeName, String recipeNutritionalInformation, double recipePrice) {
		try {
			Recipe newRecipe = new Recipe();
			newRecipe.setName(recipeName);
			newRecipe.setNutritionalInformation(recipeNutritionalInformation);

			Recipe currentRecipe = recipeService.findRecipeByObject(newRecipe);
			principle.setRecipeID(currentRecipe.getID());
			principle.setRecipe(currentRecipe);

			return new ModelAndView("viewRecipePost", "recipe", currentRecipe);
		}

		catch (Exception e) {
			return new ModelAndView("errorPage");
		}
	}
	
	/**
	 * This method will call the business service to delete the recipe.
	 * 
	 * @return ModelAndView Class
	 */
	@RequestMapping(path="/deleteRecipe", method=RequestMethod.POST)
	public ModelAndView deleteBlog()
	{
		try
		{
			recipeService.deleteRecipe(principle.getRecipe());
			
			return this.viewUserRecipe();
		}
		
		catch(Exception e)
		{
			return new ModelAndView("errorPage");
		}
	}
	
	/**
	 * This method will direct the user to the edit recipe form. 
	 * 
	 * @return ModelAndView Class
	 */
	@RequestMapping(path="/editRecipeForm", method=RequestMethod.GET)
	public ModelAndView displayEditBlogForm()
	{
		return new ModelAndView("editRecipe", "recipe", recipeService.findRecipeById(principle.getRecipeID()));
	}
	
	/**
	 * This method will call the business service to edit the recipe given and is going to send a recipe
	 * model with the parameters which are going to be updated.
	 * 
	 * @param recipe - Recipe 
	 * @param result - BindingResult
	 * @return
	 */
	@RequestMapping(path="/editRecipePost", method=RequestMethod.POST)
	public ModelAndView editBlogPost(@Valid @ModelAttribute("recipe") Recipe recipe, BindingResult result)
	{
		if(result.hasErrors())
		{
			return new ModelAndView("editRecipe", "recipe", recipe);
		}
		
		try
		{
			//Adding the user to the data base
			recipeService.editRecipe(recipe, principle.getRecipeID());
			
			return new ModelAndView("viewRecipePost", "recipe", recipeService.findRecipeById(principle.getRecipeID()));
		}
		
		catch(Exception e)
		{
			return new ModelAndView("errorPage");
		}
		
	}

	/**
	 * setRecipeBusinessService is used to inject out data service through IoC and Dependecy Injection
	 * @param service - RecipeBusinessInterface - service in order to interact with the recipe service
	 */
	@Autowired
	public void setRecipeBusinessService(RecipeBusinessInterface recipeService) {
		this.recipeService = recipeService;
	}

}

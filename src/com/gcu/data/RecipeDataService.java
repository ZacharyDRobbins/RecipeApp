package com.gcu.data;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.gcu.exception.DataServiceException;
import com.gcu.models.Principle;
import com.gcu.models.Recipe;

public class RecipeDataService implements DataAccessInterface<Recipe> {

	@SuppressWarnings("unused")
	private DataSource dataSource;
	private JdbcTemplate jdbcTemplateObject;

	@Autowired
	Principle principle;

	/**
	 * @see DataAccessInterface
	 */
	@Override
	public List<Recipe> viewAll() {
		String sqlQuery = "SELECT * FROM RECIPES";

		List<Recipe> recipeList = new ArrayList<Recipe>();

		try {
			SqlRowSet srs = jdbcTemplateObject.queryForRowSet(sqlQuery);

			while (srs.next()) {
				recipeList.add(new Recipe(srs.getInt("ID"), srs.getString("NAME"), srs.getString("DESCRIPTION"),
						srs.getString("INGREDIENTS"), srs.getString("NUTRITIONAL_INFORMATION"), srs.getInt("PRICE")));
			}
		}

		catch (Exception e) {
			e.printStackTrace();
			throw new DataServiceException(e);
		}

		return recipeList;
	}

	/**
	 * @see DataAccessInterface
	 */
	@Override
	public int create(Recipe recipe, int userID) {
		int returnNum = 0;

		try {
			String sqlInsert = "INSERT INTO `recipes` (`ID`,`NAME`, `DESCRIPTION`, `INGREDIENTS`, `NUTRITIONAL_INFORMATION`, `PRICE`, `users_ID`)"
					+ "VALUES (NULL, ?, ?, ?, ?, ?, ?);";

			returnNum += jdbcTemplateObject.update(sqlInsert, recipe.getName(), recipe.getDescription(),
					recipe.getIngredients(), recipe.getNutritionalInformation(), recipe.getPrice(), userID);
		}

		catch (Exception e) {
			e.printStackTrace();
			throw new DataServiceException(e);
		}

		return returnNum;
	}

	/**
	 * @see DataAccessInterface
	 */
	@Override
	public int update(Recipe recipe, int id) {
		int returnNum = 0;

		String sqlUpdate = "UPDATE `recipes` SET `NAME` = ?, `DESCRIPTION` = ?, `INGREDIENTS` = ?, `NUTRITIONAL_INFORMATION` = ?, `PRICE` = ?"
				+ "WHERE `recipes`.`ID` = ?;";

		try {
			returnNum += jdbcTemplateObject.update(sqlUpdate, recipe.getName(), recipe.getDescription(),
					recipe.getIngredients(), recipe.getNutritionalInformation(), recipe.getPrice(), id);
		}

		catch (Exception e) {
			e.printStackTrace();
			throw new DataServiceException(e);
		}

		return returnNum;
	}

	/**
	 * @see DataAccessInterface
	 */
	@Override
	public int delete(int id) {
		int returnNum = 0;

		String sqlDelete = "DELETE FROM `RECIPES` WHERE `RECIPES`.`ID` = ?";

		try {
			returnNum += jdbcTemplateObject.update(sqlDelete, id);
		}

		catch (Exception e) {
			e.printStackTrace();
			throw new DataServiceException(e);
		}

		return returnNum;
	}

	/**
	 * @see DataAccessInterface
	 */
	@Override
	public List<Recipe> viewByParentId(int id) {
		String sqlQuery = "SELECT * FROM RECIPES WHERE users_ID = ?";

		List<Recipe> recipeList = new ArrayList<Recipe>();

		try {
			SqlRowSet srs = jdbcTemplateObject.queryForRowSet(sqlQuery, id);

			while (srs.next()) {
				recipeList.add(new Recipe(srs.getInt("ID"), srs.getString("NAME"), srs.getString("DESCRIPTION"),
						srs.getString("INGREDIENTS"), srs.getString("NUTRITIONAL_INFORMATION"), srs.getInt("PRICE")));
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataServiceException(e);
		}

		return recipeList;
	}

	/**
	 * @see DataAccessInterface
	 */
	@Override
	public Recipe viewByObject(Recipe recipe) {
		// Creates a SQL to be filled in later
		String sql = "SELECT * FROM recipes WHERE NAME=? AND NUTRITIONAL_INFORMATION=?;";

		try {
			// Gets a set of data from the database containing the blog that matched the
			// parameters
			SqlRowSet srs = jdbcTemplateObject.queryForRowSet(sql, recipe.getName(),
					recipe.getNutritionalInformation());

			// Gets the the ID of the blog and sets the session variable of the id
			srs.next();
			principle.setRecipeID(srs.getInt("ID"));

			// returns back the blog information
			return new Recipe(srs.getInt("ID"), srs.getString("NAME"), srs.getString("DESCRIPTION"),
					srs.getString("INGREDIENTS"), srs.getString("NUTRITIONAL_INFORMATION"), srs.getInt("PRICE"));
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataServiceException(e);
		}
	}

	/**
	 * @see DataAccessInterface
	 */
	@Override
	public Recipe viewById(int id) {
		// Creates a SQL to be filled in later
		String sql = "SELECT * FROM RECIPES WHERE ID=?;";

		try {
			// Gets a set of data from the database containing the Recipe that matched the
			// parameters
			SqlRowSet srs = jdbcTemplateObject.queryForRowSet(sql, id);

			// Gets the the ID of the Recipe and sets the session variable of the id
			srs.next();
			principle.setRecipeID(srs.getInt("ID"));

			// returns back the Recipe information
			return new Recipe(srs.getInt("ID"), srs.getString("NAME"), srs.getString("DESCRIPTION"),
					srs.getString("INGREDIENTS"), srs.getString("NUTRITIONAL_INFORMATION"), srs.getInt("PRICE"));
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataServiceException(e);
		}
	}

	/**
	 * setDataSouce takes in a DataSource from our web.xml in order to create a
	 * dataSource and JDBC Template Object used to connect and perform CRUD action
	 * to the database
	 * 
	 * @param ds - DataSource - to connect the sql command to the databses
	 */
	public void setDataSource(DataSource ds) {
		this.dataSource = ds;
		this.jdbcTemplateObject = new JdbcTemplate(ds);
	}
}

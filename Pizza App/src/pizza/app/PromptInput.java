
package pizza.app;

import java.util.Scanner;
import java.sql.*;
import io.bretty.console.table.*;

/**
 *
 * Class provides methods to prompt the user and accept user input in various 
 * scenarios.
 */
public final class PromptInput {
    
    private static DatabaseService dataBaseService = new DatabaseService("localhost", "5432", "finalproject", "postgres", "admin");
    
    private PromptInput() {}
    
    
    /**
    * @param min minimum int value 
    * @param max max int value
    * @reutrn returns the user's entered int
    * 
    * Validates and returns the user's input when selecting a numbered menu option.
    */
    private static int getUserSelectedNum(int min, int max)
    {
        int input;
        
        Scanner scan = new Scanner(System.in);

        while (true)
        {
            if (! scan.hasNextInt())
            {
                String garbage;
                garbage = scan.nextLine();
                System.out.println("Please enter a valid number.");
                continue;
            }

            input = scan.nextInt();
            String garbage = scan.nextLine();

            if (input > max || input < min)
            {
                System.out.println("Please enter a valid number.");
            }

            else
            {
                break;
            }
        }
        
        return input;
    }
    
    
    /*
    * Inputs:
    * 1 -- Go to orderCrust()
    * 2 -- Go to 
    */
    public static int home()
    {
        System.out.println("Select an option below by entering the relevant number:\n"
                            + "1 -- Place an order\n"
                            + "2 -- View orders\n");
        int userInput = getUserSelectedNum(1,2);
        return userInput;
    }
    
    
    public static String orderCrust()
    {
        String selectedCrustStr = "";
        
        System.out.println("Select a crust from the table below by typing the number of the crust.");
        Table crustOptionsTable = dataBaseService.GETAsPrintableTable("SELECT ROW_NUMBER() OVER (ORDER BY crustType) AS \"Number\", crustType AS \"Crust Type\" FROM crust;");
        System.out.println(crustOptionsTable);
        
        //Find max valid input num
        int maxInputNum;
        try 
        { 
            ResultSet maxInputNumRS = dataBaseService.GET("SELECT COUNT(*) AS \"Max Number\" FROM crust;");
            maxInputNumRS.next();
            maxInputNum = maxInputNumRS.getInt(1); 
            
            //Get and validate user input
            int userInput = getUserSelectedNum(1,maxInputNum);
            
            //Get the user's selection as a string
            ResultSet selectedCrustRS = dataBaseService.GET("SELECT sub.\"Crust Type\" FROM (SELECT ROW_NUMBER() OVER (ORDER BY crustType) AS \"Number\", crustType AS \"Crust Type\" FROM crust) AS sub WHERE sub.\"Number\" = " + userInput + ";");
            selectedCrustRS.next();
            selectedCrustStr = selectedCrustRS.getString(1);
            System.out.println(selectedCrustStr);
        } 
        catch (SQLException e) { e.printStackTrace(); }
        
        //Return the user's selection
        return selectedCrustStr;
    }
    
    
    public static String orderCheese()
    {
        String selectedCheeseStr = "";
        
        System.out.println("Select a cheese from the table below by typing the number of the cheese, or type 0 for no cheese.");
        Table crustOptionsTable = dataBaseService.GETAsPrintableTable("SELECT ROW_NUMBER() OVER (ORDER BY cheeseType) AS \"Number\", cheeseType AS \"Cheese Type\" FROM cheese;");
        System.out.println(crustOptionsTable);
        
        //Find max valid input num
        int maxInputNum;
        try 
        { 
            ResultSet maxInputNumRS = dataBaseService.GET("SELECT COUNT(*) AS \"Max Number\" FROM cheese;");
            maxInputNumRS.next();
            maxInputNum = maxInputNumRS.getInt(1); 
            
            //Get and validate user input
            int userInput = getUserSelectedNum(0,maxInputNum);
            
            //If we don't want cheese, return null from the method
            if (userInput == 0)
                return null;
            
            //Get the user's selection as a string
            ResultSet selectedCheeseRS = dataBaseService.GET("SELECT sub.\"Cheese Type\" FROM (SELECT ROW_NUMBER() OVER (ORDER BY cheeseType) AS \"Number\", cheeseType AS \"Cheese Type\" FROM cheese) AS sub WHERE sub.\"Number\" = " + userInput + ";");
            selectedCheeseRS.next();
            selectedCheeseStr = selectedCheeseRS.getString(1);
            System.out.println(selectedCheeseStr);
        } 
        catch (SQLException e) { e.printStackTrace(); }
        
        //Return the user's selection
        return selectedCheeseStr;
    }
    
    
    public static void ViewOrders() {
        System.out.println(DatabaseService.GETAsPrintableTable("SELECT o.orderid, CONCAT(chef.firstname,' ',chef.lastname) \"Chef Name\", "
                                                                + "CONCAT(driver.firstname,' ',driver.lastname) \"driver name\", "
                                                                + "CONCAT(c.firstname,' ',c.lastname) \"Customer Name\""
                                                                + "FROM orders o "
                                                                + "INNER JOIN employee chef ON o.chefid = chef.employeeid "
                                                                + "INNER JOIN employee driver ON o.driverid = driver.employeeid "
                                                                + "INNER JOIN customer c ON o.customerid=c.customerid"));
    }
    
}

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;

import triggerGeneration.TriggerGenerator;
import assertionInsertion.AssertionInserter;

import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

public class Main {

    /**
     * @param args
     */
    public static void main(String[] args) {
        OptionParser parser = new OptionParser();
        OptionSpec<Void> help = parser.acceptsAll( Arrays.asList("h", "?"), "show help" );
        OptionSpec<File> file
            = parser.accepts( "f", "file" )
                    .withRequiredArg().ofType( File.class ).describedAs( "path to assertion file" );

        try {
            OptionSet options = parser.parse(args);

            if(options.has(help)){
                parser.printHelpOn( System.out );
                return;
            }

            String assertionFile = getAssertionFile(parser, file, options);
            if(assertionFile == null){
                return;
            }

            // Datenbankverbindung
            Class.forName("org.postgresql.Driver");
            String url = "jdbc:postgresql://localhost:5432/rdb_praktikum";
            Connection sql = DriverManager.getConnection(url, "henning", "henning");

            String error = AssertionInserter.insertAssertions(assertionFile, sql);

            if(error == null){
                System.out.println("Assertions have been successfully checked and saved.");
                TriggerGenerator.createAssertions(sql);
            }
            else{
                System.err.println(error);
            }
        }
        catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
        catch (OptionException optionError){
            System.err.println(optionError.getMessage());
            usage(parser);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param parser
     * @param file
     * @param options
     * @return
     */
    private static String getAssertionFile(OptionParser parser,
            OptionSpec<File> file, OptionSet options) {
        if(options.nonOptionArguments().size() > 1){
            System.err.println("Only one assertion file allowed.");
            usage(parser);
            return null;
        }
        else if(options.nonOptionArguments().size() > 0){
            return options.nonOptionArguments().get(0);
        }
        else if(options.has(file)){
            return file.value(options).getPath();
        }
        else{
            System.err.println("No assertion file given.");
            usage(parser);
            return null;
        }
    }

    /**
     * @param parser
     */
    private static void usage(OptionParser parser) {
        System.out.println("Usage:");
        try {
            parser.printHelpOn( System.out );
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}

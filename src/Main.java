import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import currentinfo.Present;
import iofiles.Action;
import iofiles.Input;
import iofiles.Movie;
import iofiles.User;
import platformpages.PlatformPage;
import platformpages.Unauthenticated;
import platformpages.Movies;
import platformpages.SeeDetails;
import platformpages.Upgrades;
import visitorpattern.ChangingPagesVisitor;
import visitorpattern.DoingOnPageActionsVisitor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static currentinfo.Notification.addRecommendation;

public final class Main {

    private Main() {
    }

    /** main method */
    public static void main(final String[] args) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        Input data = objectMapper.readValue(new File(args[0]), Input.class);

        Present.getInfo().getMoviesDatabase().addAll(data.getMovies());
        Present.getInfo().getUsersDatabase().addAll(data.getUsers());

        ArrayNode outArrayNode = objectMapper.createArrayNode();

        for (Action currentAction : data.getActions()) {
            switch (currentAction.getType()) {
                case "change page" -> {
                    ChangingPagesVisitor changingPageVisitor = new ChangingPagesVisitor();
                    if (changingPageVisitor.tryToChangePage(Present.getInfo().getCurrentPage(),
                            currentAction)) {
                        if (currentAction.getPage().equals("movies")
                            || currentAction.getPage().equals("see details")) {
                            addSuccessfulOutput(outArrayNode);
                        }
                    } else {
                        addStandardError(outArrayNode);
                    }
                }
                case "on page" -> {
                    DoingOnPageActionsVisitor onPageActionVisitor =
                            new DoingOnPageActionsVisitor();
                    if (onPageActionVisitor.tryToDoOnPageAction(Present.getInfo().getCurrentPage(),
                            currentAction)) {
                        PlatformPage upgradesPage = Upgrades.getPlatformPage();
                        if (!Present.getInfo().getCurrentPage().equals(upgradesPage)
                                && !currentAction.getFeature().equals("subscribe")) {
                            addSuccessfulOutput(outArrayNode);
                        }
                    } else {
                        addStandardError(outArrayNode);
                    }
                }
                case "database" -> {
                    switch (currentAction.getFeature()) {
                        case "add" -> {
                            if (!Present.addMovie(currentAction.getAddedMovie())) {
                                addStandardError(outArrayNode);
                            }
                        }
                        case "delete" -> {
                            if (!Present.deleteMovie(currentAction.getDeletedMovie())) {
                                addStandardError(outArrayNode);
                            }
                        }
                        default -> { }
                    }
                }
                case "back" -> {
                    if (Present.back(currentAction)) {
                        if (currentAction.getBackPage().equals(Movies.getPlatformPage())
                        || currentAction.getBackPage().equals(SeeDetails.getPlatformPage())) {
                            addSuccessfulOutput(outArrayNode);
                        }
                    } else {
                        addStandardError(outArrayNode);
                    }
                }
                default -> { }
            }
        }

        addRecommendation(outArrayNode);

        ObjectWriter objectWriter = objectMapper.writerWithDefaultPrettyPrinter();
        objectWriter.writeValue(new File(args[1]), outArrayNode);

        Present.getInfo().setCurrentPage(Unauthenticated.getPlatformPage());
        Present.getInfo().setCurrentUser(null);
        Present.getInfo().setCurrentMovieList(new ArrayList<>());

        Present.getInfo().getMoviesDatabase().clear();
        Present.getInfo().getUsersDatabase().clear();
        Present.getInfo().getPreviousPages().clear();
        Present.getInfo().getPreviousMovies().clear();
    }

    /** adds to output a successful action node */
    private static void addSuccessfulOutput(final ArrayNode outArrayNode) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode successfulOutput = objectMapper.createObjectNode();
        successfulOutput.put("error", (String) null);
        ArrayList<Movie> deepCopyMovieList = new ArrayList<>();
        for (Movie movie : Present.getInfo().getCurrentMovieList()) {
            Movie deepCopyMovie = new Movie(movie);
            deepCopyMovieList.add(deepCopyMovie);
        }
        successfulOutput.putPOJO("currentMoviesList", deepCopyMovieList);
        User deepCopyUser = new User(Present.getInfo().getCurrentUser());
        successfulOutput.putPOJO("currentUser", deepCopyUser);
        outArrayNode.add(successfulOutput);
    }

    /** adds to output a standard error node */
    private static void addStandardError(final ArrayNode outArrayNode) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode standardError = objectMapper.createObjectNode();
        standardError.put("error", "Error");
        standardError.putPOJO("currentMoviesList", new ArrayList<>());
        standardError.putPOJO("currentUser", null);
        outArrayNode.add(standardError);
    }
}

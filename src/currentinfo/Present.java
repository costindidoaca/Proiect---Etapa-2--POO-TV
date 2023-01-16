package currentinfo;

import iofiles.Action;
import iofiles.Movie;
import iofiles.User;
import platformpages.PlatformPage;
import platformpages.SeeDetails;
import platformpages.Unauthenticated;
import visitorpattern.GoingOnePageBackVisitor;

import java.util.ArrayList;
import java.util.Stack;

public final class Present {
    private static Present info = null;
    private PlatformPage currentPage;
    private User currentUser;
    private ArrayList<Movie> currentMovieList;
    private final ArrayList<Movie> moviesDatabase;
    private final ArrayList<User> usersDatabase;
    private final Stack<PlatformPage> previousPages;
    private final Stack<String> previousMovies;

    private Present() {
        currentPage = Unauthenticated.getPlatformPage();
        currentUser = null;
        currentMovieList = new ArrayList<>();
        moviesDatabase = new ArrayList<>();
        usersDatabase = new ArrayList<>();
        previousMovies = new Stack<>();
        previousPages = new Stack<>();
    }

    /** singleton javadoc */
    public static Present getInfo() {
        if (info == null) {
            info = new Present();
        }
        return info;
    }

    /** method that adds a specified movie to database and notify subscribed users */
    public static boolean addMovie(final Movie addedMovie) {
        Movie piratedMovie = new Movie(addedMovie);
        for (Movie movie : getInfo().getMoviesDatabase()) {
            if (piratedMovie.getName().equals(movie.getName())) {
                return false;
            }
        }
        Notification newNotif = new Notification(piratedMovie.getName(), "ADD");
        getInfo().getMoviesDatabase().add(piratedMovie);
        for (User user : getInfo().getUsersDatabase()) {
            for (String genre : user.getSubscribedToGenres()) {
                if (piratedMovie.getGenres().contains(genre)
                        && !piratedMovie.getCountriesBanned()
                        .contains(user.getCredentials().getCountry())) {
                    user.getNotifications().add(newNotif);
                    break;
                }
            }
        }
        return true;
    }

    /** method that deletes a specified movie from everywhere and notify subscribed users */
    public static boolean deleteMovie(final String deletedMovie) {
        for (int idx = 0; idx < getInfo().getMoviesDatabase().size(); idx++) {
            Movie movie = getInfo().getMoviesDatabase().get(idx);
            if (deletedMovie.equals(movie.getName())) {
                for (User user : getInfo().getUsersDatabase()) {
                    for (Movie purchased : user.getPurchasedMovies()) {
                        if (purchased.getName().equals(deletedMovie)) {
                            deleteMovieFromAllUserLists(deletedMovie, user);
                            Notification newNotif = new Notification(deletedMovie, "DELETE");
                            user.getNotifications().add(newNotif);
                            switch (user.getCredentials().getAccountType()) {
                                case "premium" -> user.addFreePremiumMovies(1);
                                case "standard" -> user.addTokens(2);
                                default -> { }
                            }
                            break;
                        }
                    }
                    if (getInfo().getCurrentUser() != null && getInfo().getCurrentUser()
                            .getCredentials().getName().equals(user.getCredentials().getName())) {
                        getInfo().setCurrentUser(new User(user));
                    }
                }
                getInfo().getMoviesDatabase().remove(idx);
                return true;
            }
        }
        return false;
    }

    /** method that deletes a specified movie from every user's list by the movie title */
    private static void deleteMovieFromAllUserLists(final String deletedMovie, final User user) {
        for (int idx = 0; idx < user.getPurchasedMovies().size(); idx++) {
            Movie movie = user.getPurchasedMovies().get(idx);
            if (movie.getName().equals(deletedMovie)) {
                user.getPurchasedMovies().remove(idx);
                break;
            }
        }
        for (int idx = 0; idx < user.getWatchedMovies().size(); idx++) {
            Movie movie = user.getWatchedMovies().get(idx);
            if (movie.getName().equals(deletedMovie)) {
                user.getWatchedMovies().remove(idx);
                break;
            }
        }
        for (int idx = 0; idx < user.getLikedMovies().size(); idx++) {
            Movie movie = user.getLikedMovies().get(idx);
            if (movie.getName().equals(deletedMovie)) {
                user.getLikedMovies().remove(idx);
                break;
            }
        }
        for (int idx = 0; idx < user.getRatedMovies().size(); idx++) {
            Movie movie = user.getRatedMovies().get(idx);
            if (movie.getName().equals(deletedMovie)) {
                user.getRatedMovies().remove(idx);
                break;
            }
        }
    }

    /** method that sends us to the previous page is it exists */
    public static boolean back(final Action backAction) {
        if (getInfo().getCurrentUser() != null) {
            if (getInfo().getPreviousPages().size() > 0) {
                PlatformPage lastPg = getInfo().getPreviousPages().pop();
                backAction.setBackPage(lastPg);
                if (lastPg.equals(SeeDetails.getPlatformPage())) {
                    String lastMv = getInfo().getPreviousMovies().pop();
                    backAction.setMovie(lastMv);
                }
                GoingOnePageBackVisitor goingBackVisitor = new GoingOnePageBackVisitor();
                return goingBackVisitor.tryToGoOnePageBack(Present.getInfo().getCurrentPage(),
                        backAction);
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public PlatformPage getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(final PlatformPage currentPage) {
        this.currentPage = currentPage;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(final User currentUser) {
        this.currentUser = currentUser;
    }

    public ArrayList<Movie> getCurrentMovieList() {
        return currentMovieList;
    }

    public void setCurrentMovieList(final ArrayList<Movie> currentMovieList) {
        this.currentMovieList = currentMovieList;
    }

    public ArrayList<Movie> getMoviesDatabase() {
        return moviesDatabase;
    }

    public ArrayList<User> getUsersDatabase() {
        return usersDatabase;
    }

    public Stack<PlatformPage> getPreviousPages() {
        return previousPages;
    }

    public Stack<String> getPreviousMovies() {
        return previousMovies;
    }

}

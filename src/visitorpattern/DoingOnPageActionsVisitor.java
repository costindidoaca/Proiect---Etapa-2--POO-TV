package visitorpattern;

import currentinfo.Present;
import iofiles.Action;
import iofiles.Movie;
import iofiles.User;
import platformpages.Login;
import platformpages.PlatformPage;
import platformpages.Register;
import platformpages.Unauthenticated;
import platformpages.Home;
import platformpages.Movies;
import platformpages.SeeDetails;
import platformpages.Upgrades;

import java.util.ArrayList;
import java.util.Comparator;

import static currentinfo.Constants.MAX_RATING;
import static currentinfo.Constants.PREMIUM_ACCOUNT_PRICE;

public final class DoingOnPageActionsVisitor implements Visitor {

    /** method that does a specific given on page action from a specific current page*/
    public boolean tryToDoOnPageAction(final PlatformPage currentPage, final Action action) {
        return currentPage.tryToDoActionFromThisPage(this, action);
    }

    /** method that does a specific given on page action from unauthenticated page */
    @Override
    public boolean doActionFromUnauthenticated(final Unauthenticated page, final Action action) {
        return false;
    }

    /** method that does a specific given on page action from login page */
    @Override
    public boolean doActionFromLogin(final Login page, final Action action) {
        if (action.getFeature().equals("login")) {
            for (User user : Present.getInfo().getUsersDatabase()) {
                String username = user.getCredentials().getName();
                String userPass = user.getCredentials().getPassword();
                if (username.equals(action.getCredentials().getName())
                        && userPass.equals(action.getCredentials().getPassword())) {
                    Present.getInfo().setCurrentUser(user);
                    Present.getInfo().setCurrentPage(Home.getPlatformPage());
                    return true;
                }
            }
            Present.getInfo().setCurrentPage(Unauthenticated.getPlatformPage());
        }
        return false;
    }

    /** method that does a specific given on page action from register page */
    @Override
    public boolean doActionFromRegister(final Register page, final Action action) {
        if (action.getFeature().equals("register")) {
            for (User user : Present.getInfo().getUsersDatabase()) {
                String username = user.getCredentials().getName();
                if (username.equals(action.getCredentials().getName())) {
                    Present.getInfo().setCurrentPage(Unauthenticated.getPlatformPage());
                    return false;
                }
            }
            User registeredUser = new User(action.getCredentials());
            Present.getInfo().getUsersDatabase().add(registeredUser);
            Present.getInfo().setCurrentUser(registeredUser);
            Present.getInfo().setCurrentPage(Home.getPlatformPage());
            return true;
        } else {
            return false;
        }
    }

    /** method that does a specific given on page action from home page */
    @Override
    public boolean doActionFromHome(final Home page, final Action action) {
        return false;
    }

    /** method that does a specific given on page action from movies page */
    @Override
    public boolean doActionFromMovies(final Movies page, final Action action) {
        if (action.getFeature().equals("search") || action.getFeature().equals("filter")) {
            User currentUser = Present.getInfo().getCurrentUser();
            ArrayList<Movie> currentMovieList = Present.getInfo().getCurrentMovieList();
            switch (action.getFeature()) {
                case "search" -> {
                    currentMovieList.clear();
                    for (Movie movie : Present.getInfo().getMoviesDatabase()) {
                        String userCountry = currentUser.getCredentials().getCountry();
                        if (!movie.getCountriesBanned().contains(userCountry)
                                && movie.getName().startsWith(action.getStartsWith())) {
                            currentMovieList.add(movie);
                        }
                    }
                }
                case "filter" -> {
                    currentMovieList.clear();
                    for (Movie movie : Present.getInfo().getMoviesDatabase()) {
                        String userCountry = currentUser.getCredentials().getCountry();
                        if (!movie.getCountriesBanned().contains(userCountry)) {
                            currentMovieList.add(movie);
                        }
                    }
                    if (action.getFilters().sort() != null) {
                        if (action.getFilters().sort().rating() != null) {
                            Comparator<Movie> incSort = Comparator.comparing(Movie::getRating);
                            Comparator<Movie> decSort =
                                    Comparator.comparing(Movie::getRating).reversed();
                            if (action.getFilters().sort().rating().equals("increasing")) {
                                currentMovieList.sort(incSort);
                            } else {
                                currentMovieList.sort(decSort);
                            }
                        }
                        if (action.getFilters().sort().duration() != null) {
                            Comparator<Movie> incSort = Comparator.comparing(Movie::getDuration);
                            Comparator<Movie> decSort =
                                    Comparator.comparing(Movie::getDuration).reversed();
                            if (action.getFilters().sort().duration().equals("increasing")) {
                                currentMovieList.sort(incSort);
                            } else {
                                currentMovieList.sort(decSort);
                            }
                        }
                    }
                    if (action.getFilters().contains() != null) {
                        if (action.getFilters().contains().genre() != null) {
                            currentMovieList.removeIf(movie -> !movie.getGenres()
                                    .containsAll(action.getFilters().contains().genre()));
                        }
                        if (action.getFilters().contains().actors() != null) {
                            currentMovieList.removeIf(movie -> !movie.getActors()
                                    .containsAll(action.getFilters().contains().actors()));
                        }
                    }
                }
                default -> { }
            }
            return true;
        } else {
            return false;
        }
    }

    /** method that does a specific given on page action from see details page */
    @Override
    public boolean doActionFromSeeDetails(final SeeDetails page, final Action action) {
        if (action.getFeature().equals("purchase") || action.getFeature().equals("watch")
                || action.getFeature().equals("like") || action.getFeature().equals("rate")
                || action.getFeature().equals("subscribe")) {
            User currentUser = Present.getInfo().getCurrentUser();
            Movie currentMovie = Present.getInfo().getCurrentMovieList().get(0);
            switch (action.getFeature()) {
                case "purchase" -> {
                    if (!currentUser.getPurchasedMovies().contains(currentMovie)) {
                        switch (currentUser.getCredentials().getAccountType()) {
                            case "premium" -> {
                                if (currentUser.getNumFreePremiumMovies() != 0) {
                                    currentUser.payMovieWithFreePremiumMovies();
                                    currentUser.getPurchasedMovies().add(currentMovie);
                                    return true;
                                } else {
                                    if (currentUser.getTokensCount() > 1) {
                                        currentUser.payMovieWithTokens();
                                        currentUser.getPurchasedMovies().add(currentMovie);
                                        return true;
                                    } else {
                                        return false;
                                    }
                                }
                            }
                            case "standard" -> {
                                if (currentUser.getTokensCount() > 1) {
                                    currentUser.payMovieWithTokens();
                                    currentUser.getPurchasedMovies().add(currentMovie);
                                    return true;
                                } else {
                                    return false;
                                }
                            }
                            default -> { }
                        }
                    }
                    return false;
                }
                case "watch" -> {
                    for (Movie pMovie : currentUser.getPurchasedMovies()) {
                        if (pMovie.getName().equals(currentMovie.getName())) {
                            for (Movie wMovie : currentUser.getWatchedMovies()) {
                                if (wMovie.getName().equals(currentMovie.getName())) {
                                    return true;
                                }
                            }
                            currentUser.getWatchedMovies().add(currentMovie);
                            return true;
                        }
                    }
                    return false;
                }
                case "like" -> {
                    for (Movie wMovie : currentUser.getWatchedMovies()) {
                        if (wMovie.getName().equals(currentMovie.getName())) {
                            for (Movie lMovie : currentUser.getLikedMovies()) {
                                if (lMovie.getName().equals(currentMovie.getName())) {
                                    return false;
                                }
                            }
                            currentMovie.like();
                            currentUser.getLikedMovies().add(currentMovie);
                            return true;
                        }
                    }
                    return false;
                }
                case "rate" -> {
                    if (action.getRate() > MAX_RATING) {
                        return false;
                    }
                    for (Movie wMovie : currentUser.getWatchedMovies()) {
                        if (wMovie.getName().equals(currentMovie.getName())) {
                            boolean rated = false;
                            for (Movie rMovie : currentUser.getRatedMovies()) {
                                if (rMovie.getName().equals(currentMovie.getName())) {
                                    rated = true;
                                    break;
                                }
                            }
                            currentMovie.rate(action.getRate(), rated);
                            for (User user : Present.getInfo().getUsersDatabase()) {
                                updateUserLists(currentMovie, user);
                            }
                            if (!rated) {
                                currentUser.getRatedMovies().add(currentMovie);
                            }
                            return true;
                        }
                    }
                    return false;
                }
                case "subscribe" -> {
                    ArrayList<Movie> currentMovieList = Present.getInfo().getCurrentMovieList();
                    if (currentMovieList.get(0).getGenres().contains(action.getSubscribedGenre())
                            && !currentUser.getSubscribedToGenres()
                            .contains(action.getSubscribedGenre())) {
                        currentUser.getSubscribedToGenres().add(action.getSubscribedGenre());
                        return true;
                    } else {
                        return false;
                    }
                }
                default -> { }
            }
        }
        return false;
    }

    /** method that help us update all user's lists when needed */
    private static void updateUserLists(final Movie updatedMovie, final User user) {
        for (int i = 0; i < user.getPurchasedMovies().size(); i++) {
            if (updatedMovie.getName().equals(user.getPurchasedMovies().get(i).getName())) {
                user.getPurchasedMovies().set(i, updatedMovie);
                break;
            }
        }
        for (int i = 0; i < user.getWatchedMovies().size(); i++) {
            if (updatedMovie.getName().equals(user.getWatchedMovies().get(i).getName())) {
                user.getWatchedMovies().set(i, updatedMovie);
                break;
            }
        }
        for (int i = 0; i < user.getLikedMovies().size(); i++) {
            if (updatedMovie.getName().equals(user.getLikedMovies().get(i).getName())) {
                user.getLikedMovies().set(i, updatedMovie);
                break;
            }
        }
        for (int i = 0; i < user.getRatedMovies().size(); i++) {
            if (updatedMovie.getName().equals(user.getRatedMovies().get(i).getName())) {
                user.getRatedMovies().set(i, updatedMovie);
                break;
            }
        }
    }

    /** method that does a specific given on page action from upgrades page */
    @Override
    public boolean doActionFromUpgrades(final Upgrades page, final Action action) {
        if (action.getFeature().equals("buy tokens")
                || action.getFeature().equals("buy premium account")) {
            User currentUser = Present.getInfo().getCurrentUser();
            switch (action.getFeature()) {
                case "buy tokens" -> {
                    if (Integer.parseInt(action.getCount()) <= currentUser.getIntBalance()) {
                        currentUser.payWithBalance(action.getCount());
                        currentUser.addTokens(action.getCount());
                        return true;
                    } else {
                        return false;
                    }
                }
                case "buy premium account" -> {
                    switch (currentUser.getCredentials().getAccountType()) {
                        case "standard" -> {
                            if (currentUser.getTokensCount() >= PREMIUM_ACCOUNT_PRICE) {
                                currentUser.buyPremiumAccount();
                                return true;
                            } else {
                                return false;
                            }
                        }
                        case "premium" -> {
                            return false;
                        }
                        default -> { }
                    }
                }
                default -> { }
            }
            return false;
        }
        return false;
    }
}

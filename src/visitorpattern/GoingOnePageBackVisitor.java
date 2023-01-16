package visitorpattern;

import currentinfo.Present;
import iofiles.Action;
import iofiles.Movie;
import platformpages.Login;
import platformpages.PlatformPage;
import platformpages.Register;
import platformpages.Unauthenticated;
import platformpages.Home;
import platformpages.Movies;
import platformpages.SeeDetails;
import platformpages.Upgrades;


public final class GoingOnePageBackVisitor implements Visitor {

    /** method that does the back action from any current page via visitor*/
    public boolean tryToGoOnePageBack(final PlatformPage currentPage, final Action backAction) {
        return currentPage.tryToDoActionFromThisPage(this, backAction);
    }

    /** method that does the back action from movies page */
    @Override
    public boolean doActionFromMovies(final Movies page, final Action backAction) {
        switch (backAction.getBackPage().getPageName()) {
            case "home" -> Present.getInfo().setCurrentPage(Home.getPlatformPage());
            case "movies" -> {
                Present.getInfo().getCurrentMovieList().clear();
                for (Movie movie : Present.getInfo().getMoviesDatabase()) {
                    if (!movie.getCountriesBanned().contains(Present.getInfo()
                            .getCurrentUser().getCredentials().getCountry())) {
                        Present.getInfo().getCurrentMovieList().add(movie);
                    }
                }
                Present.getInfo().setCurrentPage(Movies.getPlatformPage());
            }
            case "see details" -> {
                for (Movie movie : Present.getInfo().getMoviesDatabase()) {
                    if (movie.getName().equals(backAction.getMovie())) {
                        Present.getInfo().getCurrentMovieList().clear();
                        Present.getInfo().getCurrentMovieList().add(movie);
                        Present.getInfo().setCurrentPage(SeeDetails.getPlatformPage());
                        return true;
                    }
                }
                return false;
            }
            case "upgrades" -> Present.getInfo().setCurrentPage(Upgrades.getPlatformPage());
            default -> { }
        }
        return true;
    }

    /** method that does the back action from see details page */
    @Override
    public boolean doActionFromSeeDetails(final SeeDetails page, final Action backAction) {
        switch (backAction.getBackPage().getPageName()) {
            case "home" -> Present.getInfo().setCurrentPage(Home.getPlatformPage());
            case "upgrades" -> Present.getInfo().setCurrentPage(Upgrades.getPlatformPage());
            case "see details" -> {
                for (Movie movie : Present.getInfo().getMoviesDatabase()) {
                    if (movie.getName().equals(backAction.getMovie())) {
                        Present.getInfo().getCurrentMovieList().clear();
                        Present.getInfo().getCurrentMovieList().add(movie);
                        Present.getInfo().setCurrentPage(SeeDetails.getPlatformPage());
                        return true;
                    }
                }
                return false;
            }
            case "movies" -> {
                Present.getInfo().getCurrentMovieList().clear();
                for (Movie movie : Present.getInfo().getMoviesDatabase()) {
                    if (!movie.getCountriesBanned().contains(Present.getInfo()
                            .getCurrentUser().getCredentials().getCountry())) {
                        Present.getInfo().getCurrentMovieList().add(movie);
                    }
                }
                Present.getInfo().setCurrentPage(Movies.getPlatformPage());
            }
            default -> { }
        }
        return true;
    }

    /** method that does the back action from upgrades page */
    @Override
    public boolean doActionFromUpgrades(final Upgrades page, final Action backAction) {
        switch (backAction.getBackPage().getPageName()) {
            case "home" -> Present.getInfo().setCurrentPage(Home.getPlatformPage());
            case "movies" -> {
                Present.getInfo().setCurrentPage(Movies.getPlatformPage());
                Present.getInfo().getCurrentMovieList().clear();
                for (Movie movie : Present.getInfo().getMoviesDatabase()) {
                    if (!movie.getCountriesBanned().contains(Present.getInfo()
                            .getCurrentUser().getCredentials().getCountry())) {
                        Present.getInfo().getCurrentMovieList().add(movie);
                    }
                }
            }
            case "upgrades" -> Present.getInfo().setCurrentPage(Upgrades.getPlatformPage());
            default -> { }
        }
        return true;
    }

    /** method that does the back action from unauthenticated page */
    @Override
    public boolean doActionFromUnauthenticated(final Unauthenticated page, final Action action) {
        return false;
    }

    /** method that does the back action from login page */
    @Override
    public boolean doActionFromLogin(final Login page, final Action action) {
        return false;
    }

    /** method that does the back action from register page */
    @Override
    public boolean doActionFromRegister(final Register page, final Action action) {
        return false;
    }

    /** method that does the back action from home page */
    @Override
    public boolean doActionFromHome(final Home page, final Action action) {
        return false;
    }
}

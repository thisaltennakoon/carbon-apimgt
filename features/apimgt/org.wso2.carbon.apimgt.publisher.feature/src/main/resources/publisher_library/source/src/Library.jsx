import React, { Suspense, lazy } from 'react';
import {
    BrowserRouter as Router, Redirect, Route, Switch,
} from 'react-router-dom';
// import AuthManager from './dist_publisher_jsx_dev/publisher.js';
// import CONSTS from './dist_publisher_jsx_dev/publisher.js';
import qs from 'qs';
// import Utils from './dist_publisher_jsx_dev/publisher.js';
// import Logout from './dist_publisher_jsx_dev/publisher.js';
// import Progress from './dist_publisher_jsx_dev/publisher.js';
// import PublisherRootErrorBoundary from './dist_publisher_jsx_dev/publisher.js';
import Configurations from 'Config';
import { IntlProvider } from 'react-intl';
// import RedirectToLogin from './dist_publisher_jsx_dev/publisher.js';
// Localization
// import UnexpectedError from './dist_publisher_jsx_dev/publisher.js';
// import LoginDenied from './dist_publisher_jsx_dev/publisher.js';

import * as library from '../../dist_publisher_jsx_dev/publisher';
const ProtectedApp = lazy(() => import('./app/ProtectedApp' /* webpackChunkName: "ProtectedApps" */));

/**
 * Language.
 * @type {string}
 */
const language = (navigator.languages && navigator.languages[0]) || navigator.language || navigator.userLanguage;

/**
 * Language without region code.
 */
const languageWithoutRegionCode = language.toLowerCase().split(/[_-]+/)[0];

/**
 * Define base routes for the application
 * @returns {React.Component} base routes for the application
 */
class Publisher extends React.Component {
    /**
     *Creates an instance of Publisher.
     * @param {*} props
     * @memberof Publisher
     */
    constructor(props) {
        super(props);
        const { search } = window.location;
        const queryString = search.replace(/^\?/, '');
        /* With QS version up we can directly use {ignoreQueryPrefix: true} option */
        const queryParams = qs.parse(queryString);
        const { environment = library.Utils.getCurrentEnvironment().label } = queryParams;
        this.state = {
            userResolved: false,
            user: library.AuthManager.getUser(environment),
            messages: {},
        };
        this.updateUser = this.updateUser.bind(this);
        this.loadLocale = this.loadLocale.bind(this);
    }

    /**
     * Initialize i18n.
     */
    componentDidMount() {
        const locale = languageWithoutRegionCode || language;
        this.loadLocale(locale);
        const user = library.AuthManager.getUser();
        if (user) {
            const hasViewScope = user.scopes.includes('apim:api_view');
            if (hasViewScope) {
                this.setState({ user, userResolved: true });
            } else {
                console.log('No relevant scopes found, redirecting to login page');
                this.setState({ userResolved: true, notEnoughPermission: true });
            }
        } else {
            // If no user data available , Get the user info from existing token information
            // This could happen when OAuth code authentication took place and could send
            // user information via redirection
            const userPromise = library.AuthManager.getUserFromToken();
            userPromise
                .then((loggedUser) => {
                    if (loggedUser != null) {
                        this.setState({ user: loggedUser, userResolved: true });
                    } else {
                        console.log('User returned with null, redirect to login page');
                        this.setState({ userResolved: true });
                    }
                })
                .catch((error) => {
                    if (error && error.message === library.CONSTS.errorCodes.INSUFFICIENT_PREVILEGES) {
                        this.setState({ userResolved: true, notEnoughPermission: true });
                    } else if (error && error.message === library.CONSTS.errorCodes.UNEXPECTED_SERVER_ERROR) {
                        this.setState({ userResolved: true, unexpectedServerError: true });
                    } else {
                        console.log('Error: ' + error + ',redirecting to login page');
                        this.setState({ userResolved: true });
                    }
                });
        }
    }

    /**
     *
     *
     * @param {User} user
     * @memberof Publisher
     */
    updateUser(user) {
        this.setState({ user });
    }

    /**
     * Load locale file.
     *
     * @param {string} locale Locale name
     */
    loadLocale(locale) {
        // Skip loading the locale file if the language code is english,
        // Because we have used english defaultMessage in the FormattedText component
        // and en.json is generated from those default messages, Hence no point of fetching it
        if (locale !== 'en') {
            fetch(`${Configurations.app.context}/site/public/locales/${locale}.json`)
                .then((resp) => resp.json())
                .then((messages) => this.setState({ messages }));
        }
    }

    /**
     *
     *
     * @returns {React.Component} Render complete app component
     * @memberof Publisher
     */
    render() {
        const {
            user, userResolved, messages, notEnoughPermission, unexpectedServerError,
        } = this.state;
        const locale = languageWithoutRegionCode || language;
        if (!userResolved) {
            return <library.Progress per={5} message='Resolving user ...' />;
        }
        let checkSessionURL;
        if (user) {
            checkSessionURL = Configurations.idp.checkSessionEndpoint + '?client_id='
            + user.getAppInfo().clientId + '&redirect_uri=https://' + window.location.host
            + Configurations.app.context + '/services/auth/callback/login';
        }

        return (
            <IntlProvider locale={locale} messages={messages}>
                <library.PublisherRootErrorBoundary appName='Publisher Application'>
                    {user && (
                        <iframe
                            style={{ display: 'none' }}
                            title='iframeOP'
                            id='iframeOP'
                            src={checkSessionURL}
                            width='0px'
                            height='0px'
                        />
                    )}
                    <Router basename={Configurations.app.context}>
                        <Switch>
                            <Redirect exact from='/login' to='/apis' />
                            <Route path='/logout' component={library.Logout} />
                            <Route
                                render={() => {
                                    if (notEnoughPermission) {
                                        return <library.LoginDenied />;
                                    } else if (unexpectedServerError) {
                                        return <library.UnexpectedError />;
                                    } else if (!user) {
                                        return <library.RedirectToLogin />;
                                    }
                                    return (
                                        <Suspense fallback={<library.Progress per={10} message='Loading app ...' />}>
                                            <ProtectedApp user={user} />
                                        </Suspense>
                                    );
                                }}
                            />
                        </Switch>
                    </Router>
                </library.PublisherRootErrorBoundary>
            </IntlProvider>
        );
    }
}

export default Publisher;

## Authorization and Authentication

### Requirements
1. There will be three roles and permissions for now:
    - Admin that can see all the created user accounts and can make any user to admin
    - User that have an account can carry all the UI displayed functions: can fav the quote and can create their own quotes
    - User that have not created an account can only have read-only mode: can view the random quote but cannot fav the quote
2. User first have to sign up with user details and will be created as false for admin role
3. Admin role will be created at first when the project is loaded in the database with `admin@com` as email and `admin` as password
4. Admin can change the boolean parameter to make any user as an admin or remove from an admin role
5. Will have following api end-points:
    - POST -> /auth/signIn: sign in for an existing user with email and password form
    - POST -> /auth/signUp: sign up for a new user with user details form
    - POST -> /auth/user/{id}: let the user update the details(only the user/admin can do)
    - GET -> /auth/users: get all the users that have an account in the database(only admin can do)
    - GET -> /auth/user/{email}: get the selected user details (only the user/admin can do)
    - POST -> /auth/admin/{email}: make the user as an admin or remove from an admin role(only admin can do)
    - DELETE -> /auth/user/{email}: Delete the user from the database (only the admin can do)
    - signOut: delete the session token for the signed user(**Have to be implemented in front-end side**)
    
### How it works
When the new user creates an account through sign up endpoint then the details are stored in the database table with hashed password. Once the user login in with the email and password, first the password is matched with the stored one in database and if it is the right one then a jwt token is generated and stored in jwt play session with the key as "user". The header of the jwt token consists of type JWT and algorithm of HS256, whereas the payload consists of the case class UserToken. For the signature, I have used `play.http.secret.key` from application.conf file. And it response the jwt token in the response header once successfully signed up. 

In front-end, once we got the jwt token in the response header, then we have to store that either in local storage or in local session in web application. Now, when the user try to request any endpoint, we have to send that stored jwt token in the header to the backend api call. 


In the back-end, each API call method has the authorization layer which will be called before the endpoint methods are invoked. This authorization layer will get the jwt token from the request header and check if it is present in the jwt session with the same key "user" and if it matches then only it will invoke the api method and sends the response.

There are two authorization call method, one for admin role and one for normal logged in user. The boolean flag for isAdmin in the UserToken case class for the Admin will be true and for normal user is false and this flag will give access to admin that normal user can't. 


### Hashing the password
Postgres allows the functionality to add hashing passwords in the database. Read more [here](https://www.postgresql.org/docs/9.0/pgcrypto.html).
Since the Slick doesn't allow adding password hashing in database level, we can just hash them and insert the hashed password in the database.

It can be done implemented by [bcrypt](https://en.wikipedia.org/wiki/Bcrypt). 

- Java implementation of [jBCrypt](https://www.mindrot.org/projects/jBCrypt/)
- Scala wrapper of [Java jBCrypt](https://github.com/t3hnar/scala-bcrypt)
- Articles:
    - [Better password hashing in Play 2.2 (Java)](http://rny.io/playframework/bcrypt/2013/10/22/better-password-hashing-in-play-2.html)
    - [Passwords & hash functions (Simply Explained)](https://www.youtube.com/watch?v=cczlpiiu42M&ab_channel=SimplyExplained)
    
### Have used JWT to generate user auth token
- scala-jwt library used:
    - [jwt-scala](https://github.com/pauldijou/jwt-scala) and [example](http://pauldijou.fr/jwt-scala/)
    - https://jwt.io
    - Another library - haven't used: [Silhouette](https://github.com/adamzareba/play-silhouette-rest-slick)

## Secret key for JWT encryption
- Is same as `play.http.secret.key` in application.conf file
- More about [JWT](https://www.youtube.com/watch?v=7Q17ubqLfaM)

### Keep in mind
- Have to implement the functionality where the user forgets the password (might need to implement email verification system)
- Have to enter the admin password as hashing when the database is initially created, run by play evolution in database migration.
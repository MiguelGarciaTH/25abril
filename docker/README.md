
Run docker files:

Build backend image:
```sh
 docker build -t arquivo-rest -f arquivo-rest ../arquivo-rest
```

Build frontend image:
```sh
npm run build
```

```sh
 docker build -t arquivo-web -f arquivo-web ../arquivo-web
```
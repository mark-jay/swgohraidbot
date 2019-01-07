### About

Me having fun with discord bots, ocr and jenetics. Nothing of a value for anyone but me.
 
### Build

```bash
./mvnw clean install
```

### Build docker image

```bash
./mvnw clean install -DskipTests -Pdocker
```

### Run docker

```bash
docker run -v "$HOME"/.discord/token:/root/.discord/token markjay/swgohraidbot-discrodbot
```

### Deliver docker and run 

```bash

# installing docker
ssh $TARGET_HOST curl -fsSL https://get.docker.com -o /tmp/get-docker.sh
ssh $TARGET_HOST sudo sh get-docker.sh
sudo usermod -aG docker <USER_USER>

# delivering image
docker save markjay/swgohraidbot-discrodbot -o /tmp/image.img
scp /tmp/image.img $TARGET_HOST:/tmp/image.img 
ssh $TARGET_HOST docker load -i /tmp/image.img

# running
docker run -d --restart unless-stopped -v "$HOME"/.discord/token:/root/.discord/token markjay/swgohraidbot-discrodbot

```

### Links

[setting up discord bot](https://medium.com/discord-bots/making-a-basic-discord-bot-with-java-834949008c2b)
[discord bot docs](https://discordapp.com/developers/docs/intro)
[javacord](https://github.com/Javacord/Javacord)

add bot to a server:
```
https://discordapp.com/oauth2/authorize?client_id=530447444157136916&scope=bot&permissions=67584
```


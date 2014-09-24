
Plugin permettant de copier une propriété YAML.

exemple de fichier .yml :

    1.	server:
    2.		tomcat:
    3.			access_log_enabled: true
    4.			basedir: target/tomcat
    5.		port: 8180

    7.	security :
    8.		basic:
    9.		enabled: false

    11.	paybox:
    12.		service:
    13.			debitCarte:
    14.				payboxUrl: http://10.199.54.202/pollux/stubs/debitCarte
    15.				localServicePath: /paybox/debitCarte
    16.			pppsurl:
    17.				payboxUrl: http://10.199.54.202/pollux/stubs/ppps
    18.				localServicePath: /PPPS


Ce plugin permet de faire un clic droit sur `localServicePath` (ligne 15) et de copier `paybox.service.debitCarte.localServicePath` dans le presse-papier.

Le plugin ignore les lignes blanches et les lignes commentées.


Deploy
------

Pour construire le jar :

- Make your project (`Build | Make Project`)
- Prepare plugin (`Build | Prepare Plugin Module <module name> for Deployment`)

Install
-------

`Settings | Plugins | Install plugin from disk...`


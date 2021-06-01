Breaking glass on the production or staging database is a way to save production or staging outages if something extreme happens that cannot be patched with software patches. Staging is the wild-west, so we can just `curl -X POST http://staging.seattle.civiform.com/dev/seed/clear` if we need to truncate all of the tables in staging.

To break glass, 

1. Sign into the AWS management console and open up the cloud shell
![Windows task manager virtualization check](https://drive.google.com/uc?id=1I7pWoud4cm-oB7KBZGsuxtcMTv_dkWLe)

2. Clone civiform: `git clone https://github.com/seattle-uat/civiform.git`

3. Run `bin/breakglass-db-access prod` to break glass into prod. Or just `bin/breakglass-db-access` to break glass into staging.

4. Make sure to exit out of the postgresql shell to terminate the ec2 instance and delete the emergency DB security group.
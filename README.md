Karting Results App
===================

An application to extract and aggregate go-karting results. Specifically built for the
[Chicago F1 group](http://www.facebook.com/ChicagoF1).

At least a racer per split will be asked to forward the results email he/she receives from the karting group
to a specific email address, which the application will be able to process.
As of now, the email address for this purpose is `tomscript13@gmail.com`.

If you're interested in seeing the results, you can go directly
[here](https://github.com/fedeoasi/karting-results-app/tree/master/output), and
[here](https://github.com/fedeoasi/karting-results-app/tree/master/output/edition)
for the aggregated results.

## How it works

The application fetches the Melrose Park Indoor Grand Prix emails and creates a csv file for each split,
plus an additional aggregated file for all the results in the same date.
This way we'll be able to easily track who won and when.

## Build & Run

We use sbt (Simple/Scala Build Tool) as build system, which provides a shell where we can execute multiple commands.
To run the tests:

```sh
$ ./sbt
> test
```

To run the main application you'll need first to set the following environment variables:

`KRA_EMAIL`: email address (for now tomscript13@gmail.com)

`KRA_PWD`: the above email's password

Now you're ready to run the app:
```sh
$ ./sbt
> run
```

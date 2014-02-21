The `siriusShell.groovy` script is the easiest way to get started
with sirius. The script provides an interactive shell to
perform crud operations against a HashMap backend.

####Installing Sirius Library

The sirius library needs to be installed localy for the script to work
properly.

```bash
git clone https://github.com/Comcast/sirius.git
cd sirius
mvn -DskipTests install
```

####Installing Groovy

You will need to install groovy to get the example to work. If you
have access to bash, you can run the following

```bash
curl -s get.gvmtool.net | bash
source ~/.gvm/bin/gvm-init.sh
gvm install groovy
```

Most package managers include groovy (ie brew install groovy)

For other installation options see
http://groovy.codehaus.org/Installing+Groovy

####Running a Single Node

By default the script runs in single node mode. To run, execute
`groovy siriusShell.groovy`. After running the script, the following
commands will be available to you

```
output from running command [help]

 - data commands
   > get <key>          gets value for a key
   > put <key> <value>  creates or updates value for key
   > delete <key>       deletes value for key
   > data               prints all data

 - administration commands
   > help               prints this message
   > stop               stops sirius
   > start              starts sirius
   > exit               exits the program

```

By default the script runs on port 2552 and creates temporary
`UberStore` and `cluster.config` files. After running some crud
commands in the shell, run stop then start to watch sirius ingest the
transaction log. When exit is called, the `UberStore` and
`cluster.config` are deleted. `UberStore` might not delete if the
script is run in Windows.

####Running Multiple Nodes (nix systems only)

The script can accept two arguments to enable multiple nodes

```bash
groovy siriusShell.groovy <cluster config path> <port>
```

The cluster config defines all of the node endpoints.  Assuming the
cluster config file contains

```
  akka.tcp://sirius-system@localhost:2552/user/sirius
  akka.tcp://sirius-system@localhost:2553/user/sirius
  akka.tcp://sirius-system@localhost:2554/user/sirius
```

Then run the following in three different terminals

```
  terminal1 -> groovy siriusShell.groovy /path/to/config 2552
  terminal2 -> groovy siriusShell.groovy /path/to/config 2553
  terminal3 -> groovy siriusShell.groovy /path/to/config 2554
```

Pick any terminal and start firing off crud commands against sirius.
Each terminal should replicate the commands. If you shutdown one
node with the `stop` command, the two remaining nodes can
still handle crud operations. If the downed node is then started
again, within 30 seconds it should catch up to the other two nodes.

For sirius to operate properly, more than half of the nodes must be
operating.  To ensure this constraint, sirius will refuse any write
operations if too many nodes are down. To demonstrate this, stop two
of three nodes and execute either `put` or `delete` on the remaining
node.

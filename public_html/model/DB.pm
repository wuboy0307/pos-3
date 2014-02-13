package DB;

############################################
#
#   Filename:  DAO.pm (package DAO)
#   Author: 
#
############################################

use strict;
use DBI;
use Log::Log4perl;
use Sys::Hostname;
use Data::Dumper;
use String::Util 'trim';

my $logger = Log::Log4perl->get_logger('opuma');

############################################
#
#   Constructor
#
############################################
sub new {
    my $className = shift;
    my $dbh = getConnection();
    my $self = {
        'dbh' => $dbh
    };
    bless $self, $className;
    return $self;
}

############################################
#
#   getConnection()
#
#   Connects to MySQL using the config file
#   named for the host (if found)
#
#   Config file naming convention:
#   host.name.com.db.cfg
#
#   Config entry is name=value;
#   database=myDatabaseName;
#
#   Config keys:
#   database, databaseHost, user, password
#
############################################
sub getConnection {

    my $host = hostname;
    #$logger->debug("HOSTNAME $host");

    my $databaseHost;
    my $database;
    my $user;
    my $password;

    open my $fh, '<', "../cfg/$host.db.cfg" or $logger->error("Can't open $host DB config file");
    if (defined $fh) {
        my $data = do { local $/; <$fh> };
        if ($data =~ /database=(.*);/) {
            $database = $1;
        }
        if ($data =~ /hostname=(.*);/) {
            $databaseHost = $1;
        }
        if ($data =~ /user=(.*);/) {
            $user = $1;
        }
        if ($data =~ /password=(.*);/) {
            $password = $1;
        }
    }

    my $dbh;
    $dbh = DBI->connect("DBI:mysql:host=$databaseHost;database=$database",
                        "$user","$password")
           or $logger->error("Couldn't connect to database: $DBI::errstr");
    die "My script is having problems.  Sorry.  Love, Geoff" unless (defined $dbh);

    return $dbh;
}
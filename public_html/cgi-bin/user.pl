#!/usr/bin/perl

############################################
#
#   Filename:  user.pl
#   Accepts HTTP parameters as input and returns JSON for the
#   User storage (the 'user' table).
#
#   See the Constants package in the model directory for the literal
#   string values.
#
#   Following the pattern for every POS service, an "action" is required,
#   passed in as parameter, FIELD_ACTION
#
#   Supports the following actions (required fields):
#   ACTION_GET_ALL
#   ACTION_LOGIN    (FIELD_LOGIN,FIELD_PIN)
#   ACTION_ADD      (FIELD_LOGIN,FIELD_PIN,FIELD_IS_ADMIN)
#   ACTION_UPDATE   (FIELD_LOGIN,FIELD_PIN,FIELD_IS_ADMIN,FIELD_IS_ACTIVE,FIELD_UPDATE_USER)
#   ACTION_DELETE   (FIELD_LOGIN)
#
############################################

use strict;
# Libray path designed to run on the Texas State CS student servers
use lib qw(/home/Students/g_m108/perllib /home/Students/g_m108/perllib/x86_64-linux-thread-multii ../model);

# Log4Perl and Data Dumper for logging and debugging
use Data::Dumper;
use Log::Log4perl qw(:easy);
    Log::Log4perl->easy_init($DEBUG);
Log::Log4perl::init('../cfg/log4perl.conf');

my $logger = Log::Log4perl->get_logger('opuma');

# CGI and JSON handling
use CGI;
use JSON;
# POS database access and field constants
use UserDAO;
use Constants;

my $cgi = new CGI;

# The action
my $action = $cgi->param(Constants::FIELD_ACTION);
# The fields
my $login = $cgi->param(Constants::FIELD_LOGIN);
my $pin = $cgi->param(Constants::FIELD_PIN);
my $isAdmin = $cgi->param(Constants::FIELD_IS_ADMIN);
my $isActive = $cgi->param(Constants::FIELD_IS_ACTIVE);
my $id = $cgi->param(Constants::FIELD_USER_ID);
my $deviceID = $cgi->param(Constants::FIELD_DEVICE_ID);
my $updateUser = $cgi->param(Constants::FIELD_UPDATE_USER);

# Output the content type for the HTTP response
print "Content-type: application/json\n\n";

$logger->debug("USER.PL: $action");
$logger->debug("ID: $id");

# The UserDAO for access to the 'user' table
my $dao = new UserDAO();
# The return value
my $a = {};
# Action drives what DAO method to use
if (Constants::ACTION_ADD eq $action) {
    if (defined($login) && defined($pin) && defined($isAdmin)) {
        $a = $dao->addUser($login,$pin,$isAdmin);
    } else {
        $a->{'returnMessage'} = "Login, pin, and isAdmin are required.";
        $a->{'returnCode'} = Constants::ERROR_MISSING_REQUIRED_FIELDS;
    }
} elsif (Constants::ACTION_DELETE eq $action) {
    if (defined($login)) {
        $a = $dao->deleteUser($login);
    } else {
        $a->{'returnMessage'} = "Login is required.";
        $a->{'returnCode'} = Constants::ERROR_MISSING_REQUIRED_FIELDS;
    }
} elsif (Constants::ACTION_UPDATE eq $action) {
    if (defined($login) && defined($pin) && defined($isAdmin) && defined($isActive)) {
        $a = $dao->updateUser($login,$pin,$isAdmin,$isActive,$id);
    } else {
        $a->{'returnMessage'} = "Login, pin, isActive, and isAdmin are required.";
        $a->{'returnCode'} = Constants::ERROR_MISSING_REQUIRED_FIELDS;
    }
} elsif (Constants::ACTION_LOGIN eq $action) {
    if (defined($login) && defined($pin)) {
        $a = $dao->login($login,$pin);
    } else {
        $a->{'returnMessage'} = "Login and pin are required.";
        $a->{'returnCode'} = Constants::ERROR_MISSING_REQUIRED_FIELDS;
    }
} elsif (Constants::ACTION_GET_ALL eq $action) {
    $a = $dao->getUsers();
} else {
    $a->{'returnMessage'} = "No action parameter given.";
    $a->{'returnCode'} = Constants::ERROR_NO_ACTION;
}

# Encode and return the JSON response based on the data
my $json = encode_json $a;
print "$json\n";

$logger->debug("USER RESPONSE: $json");
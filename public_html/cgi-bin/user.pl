#!/usr/bin/perl

############################################
#
#   Filename:  login.pl
#   Author: Geoff Marinski
#
#   Serves the AJAX calls for the login service.
#
#   Returns JSON.  So I guess it is AJAJ.
#
############################################

use strict;
use lib qw(/home/Students/g_m108/perllib /home/Students/g_m108/perllib/x86_64-linux-thread-multii ../model);

use Data::Dumper;
use Log::Log4perl qw(:easy);
    Log::Log4perl->easy_init($DEBUG);
Log::Log4perl::init('../cfg/log4perl.conf');

my $logger = Log::Log4perl->get_logger('opuma');

use CGI;
use UserDAO;
use JSON;

my $cgi = new CGI;

my $action = $cgi->param('action');
my $login = $cgi->param('login');
my $pin = $cgi->param('pin');
my $isAdmin = $cgi->param('is_admin');
my $isActive = $cgi->param('is_active');
my $id = $cgi->param('user_id');
my $deviceID = $cgi->param('device_id');

print "Content-type: application/json\n\n";
# application/json

$logger->debug("USER.PL: $action");
$logger->debug("ID: $id");

my $dao = new UserDAO();
my $a = {};
if ("add" eq $action) {
    if (defined($login) && defined($pin) && defined($isAdmin)) {
        $a = $dao->addUser($login,$pin,$isAdmin);
    } else {
        $a->{'returnMessage'} = "Login, pin, and isAdmin are required.";
        $a->{'returnCode'} = -1;
    }
} elsif ("delete" eq $action) {
    if (defined($login)) {
        $a = $dao->deleteUser($login);
    } else {
        $a->{'returnMessage'} = "Login is required.";
        $a->{'returnCode'} = -98;
    }
} elsif ("update" eq $action) {
    if (defined($login) && defined($pin) && defined($isAdmin) && defined($isActive)) {
        $a = $dao->updateUser($login,$pin,$isAdmin,$isActive,$id);
    } else {
        $a->{'returnMessage'} = "Login, pin, isActive, and isAdmin are required.";
        $a->{'returnCode'} = -98;
    }
} elsif ("login" eq $action) {
    if (defined($login) && defined($pin)) {
        $a = $dao->login($login,$pin);
    } else {
        $a->{'returnMessage'} = "Login and pin are required.";
        $a->{'returnCode'} = -98;
    }
} elsif ("getAll" eq $action) {
    $a = $dao->getUsers();
} else {
    $a->{'returnMessage'} = "No action parameter given.";
    $a->{'returnCode'} = -99;
}

my $json = encode_json $a;
print "$json\n";

$logger->debug("USER RESPONSE: $json");
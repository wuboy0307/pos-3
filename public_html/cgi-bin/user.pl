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
use DAO;
use JSON;

my $cgi = new CGI;

my $action = $cgi->param('action');
my $login = $cgi->param('login');
my $pin = $cgi->param('pin');
my $isAdmin = $cgi->param('is_admin');
my $isActive = $cgi->param('is_active');
my $id = $cgi->param('user_id');

print "Content-type: application/json\n\n";
# application/json

$logger->debug("USER.PL: $action");
$logger->debug("ID: $id");

my $dao = new DAO();
my $a;
if ("add" eq $action) {
    $a = $dao->addUser($login,$pin,$isAdmin);
} elsif ("delete" eq $action) {
    $a = $dao->deleteUser($login);
} elsif ("update" eq $action) {
    $a = $dao->updateUser($login,$pin,$isAdmin,$isActive,$id);
} elsif ("getAll" eq $action) {
    $a = dao->getUsers();
} else {
    $a = {};
    $a->{'returnMessage'} = "No action parameter given.";
    $a->{'returnCode'} = -1;
}

my $json = encode_json $a;
print "$json\n";

$logger->debug("USER RESPONSE: $json");
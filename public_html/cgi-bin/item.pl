#!/usr/bin/perl

############################################
#
#   Filename:  item.pl
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
use ItemDAO;
use JSON;

my $cgi = new CGI;

my $action = $cgi->param('action');
my $description = $cgi->param('description');
my $id = $cgi->param('item_id');
my $price = $cgi->param('price');
my $userID = $cgi->param('user_id');
my $deviceID = $cgi->param('device_id');

print "Content-type: application/json\n\n";
# application/json

$logger->debug("USER.PL: $action");
$logger->debug("ID: $id / $deviceID");

my $dao = new ItemDAO();
my $a = {};
if ("getAll" eq $action) {
    $a = $dao->getItems();
} elsif ("reset" eq $action) {
    $a = $dao->reset($deviceID);
} elsif ("sync" eq $action) {
    $a = $dao->sync($deviceID);
} else {
    $a->{'returnMessage'} = "No action parameter given.";
    $a->{'returnCode'} = -1;
}

my $json = encode_json $a;
print "$json\n";

$logger->debug("USER RESPONSE: $json");
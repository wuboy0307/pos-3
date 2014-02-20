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

my $login = $cgi->param('login');
my $password = $cgi->param('pin');
my $deviceID = $cgi->param('device_id');

print "Content-type: application/json\n\n";
# application/json

$logger->debug("LOGIN.PL: $login");

my $dao = new DAO();
my $a = $dao->login($login,$password);

my $json = encode_json $a;
print "$json\n";

$logger->debug("LOGIN RESPONSE: $json");
#!/usr/bin/perl

############################################
#
#   Filename:  heartbeat.pl
#   Author: Geoff Marinski
#
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
my $deviceID = $cgi->param('device_id');

print "Content-type: application/json\n\n";
# application/json

$logger->debug("ITEM.PL: $action");
$logger->debug("ID: $deviceID");

my $dao = new DAO();
my $a = {};
if ("ping" eq $action) {
    $a = $dao->getSettings();
    if (defined $a && $a->{'returnCode'} == 0) {
        if ($a->{'data'}->{'simulate_down_nice'} eq '1') {
            $a->{'returnMessage'} = "Simulating down.";
            $a->{'returnCode'} = -97;
        } elsif ($a->{'data'}->{'simulate_down_broken'} eq '1') {
            $a = undef;
        }
    } else {
        $a->{'returnMessage'} = "Problem with backend connection.";
        $a->{'returnCode'} = -96;
    }
} else {
    $a->{'returnMessage'} = "No action parameter given.";
    $a->{'returnCode'} = -99;
}

my $json = encode_json $a;
print "$json\n";

$logger->debug("HEARTBEAT RESPONSE: $json");
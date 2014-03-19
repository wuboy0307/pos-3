package CartDAO;
use DB;
our @ISA = qw(DB);    # inherits from DB

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
use Constants;

my $logger = Log::Log4perl->get_logger('opuma');

sub add {
    my $self = shift;
    my $cart = shift;
    my $deviceID = shift;
    
    $logger->debug(Dumper($cart));
    
    my $id = $cart->{'cart_id'};
    my $total = $cart->{'total'};
    my $paymentCard = $cart->{'payment_card'};
    my $items = $cart->{'items'};
    my $pin = $cart->{'payment_pin'};
    my $taxAmount = $cart->{'tax_amount'};
    my $customerEmail = $cart->{'customer_email'};
    my $userID = $cart->{'user_id'};
    my $subTotal = $cart->{'subtotal'};
    my $taxRate = $cart->{'tax_rate'};
    
    
    
    return;
}
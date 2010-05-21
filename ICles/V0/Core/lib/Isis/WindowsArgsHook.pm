package Isis::WindowsArgsHook;

use strict;

=head1 NAME

 Isis::WindowsArgsHook - Convertit les arguments windows à la volée
 
=head1 SYNOPSIS

 use Isis::WindowsArgsHook;

ou bien

 perl -MIsis::WindowsArgsHook script.pl
 
=head1 DESCRIPTION


=head1 AUTHOR

BV Associates, 2009

=cut

# stuff to make standard module
my $start_date;
BEGIN {
    use Exporter   ();
    our (@ISA, @EXPORT_OK);
    @ISA         = qw(Exporter);
    @EXPORT_OK   = qw();


    map { s/%(\w+)%/$ENV{$1}/g } @ARGV;
    @ARGV = grep { defined($_) && $_ ne '' } @ARGV;
}

1;
